package com.fut.chatbot.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import androidx.preference.PreferenceManager;

import com.fut.chatbot.R;
import com.fut.chatbot.activity.ChatbotActivity;
import com.fut.chatbot.database.AppDatabase;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Answer;
import com.fut.chatbot.model.Ask;
import com.fut.chatbot.model.Broadcast;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.model.Poll;
import com.fut.chatbot.receiver.NetworkChangeReceiver;
import com.fut.chatbot.util.AppExecutors;
import com.fut.chatbot.util.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class ChatService extends Service {

    // Binder given to clients
    private final IBinder binder = new MessagingBinder();
    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private AppDatabase mDb;
    private SharedPreferences sharedPref;

    private boolean isAppRunning = false;

    public void setAppRunning(boolean isAppRunning){
        this.isAppRunning = isAppRunning;
        Log.e("RUN:::::::", "App is Running ?" + isAppRunning);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
    }

    private void startForegroundService() {
        Intent intent = new Intent(getApplicationContext(), ChatbotActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplication(), this.getString(R.string.channel_chatbot_messages_id));
        Notification notification = mBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDb = AppDatabase.getInstance(getApplicationContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("SETUP")) {
                BroadcastReceiver broadcastReceiver = new NetworkChangeReceiver();
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                getApplication().registerReceiver(broadcastReceiver, filter);
                if(mStompClient == null || !mStompClient.isConnected()) {
                    reconnect();
                }
            } else if (intent.getAction().equals("CONNECT")) {
                if(mStompClient == null || !mStompClient.isConnected()){
                    reconnect();
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    private void resetSocketSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void iniWebSocket(){
        resetSocketSubscriptions();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("key", Constants.KEY);
        headerMap.put("phone", sharedPref.getString(getString(R.string.prompt_phone), null));
        headerMap.put("code", sharedPref.getString(getString(R.string.prompt_code), null));

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Constants.WEB_SOCKET_BASE_URL, headerMap);
        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
        Disposable initDisposable = mStompClient.lifecycle().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.e("OPEN", "Stomp connection opened");

                    AppExecutors.getInstance().networkIO().execute(this::sendOnline);

                    AppExecutors.getInstance().diskIO().execute(() -> {
                        for(Chat chat: mDb.chatDao().getAllByTypeAndSent(Chat.ChatType.MINE, false)){
                            AppExecutors.getInstance().networkIO().execute(() -> sendChat(chat));

                        }
                        for(Chat chat: mDb.chatDao().getAllByTypeAndSent(Chat.ChatType.BOT, false)){
                            AppExecutors.getInstance().networkIO().execute(() -> sendDelivery(chat));
                        }
                    });
                    break;

                case ERROR:
                    Log.e("ERROR", "Error", lifecycleEvent.getException());
                    break;

                case CLOSED:
                    Log.e("CLOSED", "Stomp connection closed");
                    break;
                case FAILED_SERVER_HEARTBEAT:
                    Log.e("HEARTBEAT_ERROR", "Stomp failed server heartbeat");
                    break;
            }
        }, throwable -> Log.e("WEB_SOCKET", throwable.getMessage(), throwable));
        compositeDisposable.add(initDisposable);

        Disposable topicDisposable = mStompClient.topic("/private/reply")
                .subscribe(this::receiveChat, throwable -> Log.e("ERROR SUBSCRIBE", "Unable to subscribe"));
        compositeDisposable.add(topicDisposable);

        mStompClient.connect();
    }

    private void reconnect(){
        disconnect();
        resetSocketSubscriptions();
        iniWebSocket();
    }

    private void disconnect(){
        if(mStompClient != null && mStompClient.isConnected()) {
            mStompClient.disconnect();
        }
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

    private void receiveChat(StompMessage stompMessage){
        Message message = Constants.GSON.fromJson(stompMessage.getPayload(), Message.class);
        Log.e("CHAT",  stompMessage.getPayload());

        String notificationBody;
        String notificationTitle;

        if(message.getType() == Message.MessageType.ANSWER) {
            Answer answer = message.getAnswer();
            notificationTitle = getString(R.string.app_name);
            if(answer.getBody() != null && !answer.getBody().isEmpty()){
                notificationBody = answer.getBody();
            }else{
                notificationBody = answer.getPreference().getName();
            }
        }else if(message.getType() == Message.MessageType.BROADCAST){
            Broadcast broadcast = message.getBroadcast();
            notificationTitle = "New Message From: " + broadcast.getContributor().getFirstName() + " " + broadcast.getContributor().getLastName();
            notificationBody = broadcast.getBody();
        }else{
            Poll poll = message.getPoll();
            notificationTitle = "New Poll, Titled: " + poll.getTitle();
            notificationBody = poll.getBody();
        }


        Chat chat = new Chat();
        chat.setType(Chat.ChatType.BOT);
        chat.setTime(message.getTime());
        chat.setData(Constants.GSON.toJson(message));

        AppExecutors.getInstance().diskIO().execute(() ->{
            Chat lastChat = mDb.chatDao().getLast();
            if(lastChat == null || !Constants.DATE_FORMAT.format(lastChat.getTime()).equals(Constants.DATE_FORMAT.format(new Date()))){
                Chat dateChat = new Chat();
                dateChat.setTime(new Date());
                dateChat.setType(Chat.ChatType.DATE);
                dateChat.setData(Constants.GSON.toJson(new Date()));
                mDb.chatDao().addChat(dateChat);
            }
            long insertId = mDb.chatDao().addChat(chat);
            Chat savedChat = mDb.chatDao().getById(insertId);
            AppExecutors.getInstance().mainThread().execute(() -> sendDelivery(savedChat));
        });
        if(!isAppRunning){
            createNotification(message.getId(), notificationTitle, notificationBody, message.getTime());
        }
        startTTS(notificationBody);
    }

    private void startTTS(String text){
        if(sharedPref.getBoolean(getString(R.string.preference_key_tts_enabled), false)) {
            Intent speechIntent = new Intent(getApplicationContext(), AppTextToSpeechService.class);
            speechIntent.putExtra("text", text);
            getApplicationContext().startService(speechIntent);
        }
    }

    @SuppressLint("CheckResult")
    public void ask(String text){
        Ask ask = new Ask();
        ask.setText(text);

        Chat chat = new Chat();
        chat.setTime(new Date());
        chat.setType(Chat.ChatType.MINE);
        chat.setSent(false);
        chat.setData(Constants.GSON.toJson(ask));

        AppExecutors.getInstance().diskIO().execute(() -> {
            Chat lastChat = mDb.chatDao().getLast();
            if(lastChat == null || !Constants.DATE_FORMAT.format(lastChat.getTime()).equals(Constants.DATE_FORMAT.format(new Date()))){
                Chat dateChat = new Chat();
                dateChat.setTime(new Date());
                dateChat.setType(Chat.ChatType.DATE);
                dateChat.setData(Constants.GSON.toJson(new Date()));
                mDb.chatDao().addChat(dateChat);
            }
            long chatId = mDb.chatDao().addChat(chat);
            chat.setId(chatId);
            sendChat(chat);
        });
    }

    public void sendChat(Chat chat) {
        if (mStompClient != null) {
            AppExecutors.getInstance().networkIO().execute(() -> mStompClient.send("/send", chat.getData()).compose(applySchedulers()).subscribe(() -> {
                chat.setSent(true);
                AppExecutors.getInstance().diskIO().execute(() -> mDb.chatDao().updateChat(chat));
            }, throwable -> {
                Log.e("WEB_SOCKET", throwable.getMessage(), throwable);
                reconnect();
            }));
        }
    }

    public void sendDelivery(Chat chat){
        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);
        if(mStompClient != null) {
            AppExecutors.getInstance().networkIO().execute(() -> mStompClient.send("/delivery", String.valueOf(message.getId())).compose(applySchedulers()).subscribe(() -> {
                chat.setSent(true);
                AppExecutors.getInstance().diskIO().execute(() -> mDb.chatDao().updateChat(chat));
            }, throwable -> Log.e("WEB_SOCKET", throwable.getMessage(), throwable)));
        }
    }

    public void sendOnline(){
        if(mStompClient != null) {
            AppExecutors.getInstance().networkIO().execute(() -> mStompClient.send("/online")
                    .compose(applySchedulers()).subscribe(() -> {}, throwable -> {}));
        }
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void createNotification(int id, String title, String body, Date time){
        Intent intent = new Intent(getApplicationContext(), ChatbotActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Person person = new Person.Builder()
                .setBot(true)
                .setImportant(true)
                .setName(title)
                .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.fut_chatbot))
                .setKey(getPackageName())
                .build();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), this.getString(R.string.channel_chatbot_messages_id))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColorized(true)
                .setColor(Color.argb(1, 156, 39, 176))
                .setStyle(new NotificationCompat.MessagingStyle(person)
                        .addMessage(body, time.getTime(), person))
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class MessagingBinder extends Binder {
        public ChatService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChatService.this;
        }
    }
}
