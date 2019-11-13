package com.fut.chatbot.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.fut.chatbot.R;
import com.fut.chatbot.adapter.ChatAdapter;
import com.fut.chatbot.database.AppDatabase;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Answer;
import com.fut.chatbot.model.Extra;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.model.Poll;
import com.fut.chatbot.model.PollItem;
import com.fut.chatbot.service.ChatService;
import com.fut.chatbot.util.AppExecutors;
import com.fut.chatbot.util.Constants;
import com.fut.chatbot.viewmodel.ChatbotViewModel;
import com.fut.chatbot.viewmodel.ChatbotViewModelFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity implements ChatAdapter.ChatClickListener {

    private static final int REQUEST_SPEECH_RECOGNIZER = 467;

    private ChatService chatService;
    private ChatAdapter chatAdapter;
    private SharedPreferences sharedPref;
    private AppDatabase mDb;
    private ChatbotViewModel viewModel;

    private Toolbar mainToolbar;
    private Toolbar selectionToolbar;

    private EditText edtAskText;

    private boolean isBound = false;
    private boolean isRunning = false;
    private boolean isSelectionMode = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        mainToolbar = findViewById(R.id.toolbar);
        selectionToolbar = findViewById(R.id.toolbar_selection);
        selectionToolbar.setVisibility(View.GONE);

        setSupportActionBar(mainToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        TextView titleView = mainToolbar.findViewById(R.id.title);
        titleView.setText(R.string.app_name);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        edtAskText = findViewById(R.id.edt_ask_text);

        ImageButton btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(view -> sendChat());

        ImageButton btnSpeech = findViewById(R.id.btn_speech);
        btnSpeech.setOnClickListener(view -> startSpeechToText());

        chatAdapter = new ChatAdapter(this, this);
        RecyclerView recyclerViewChats = findViewById(R.id.recycler_view_chats);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChats.setAdapter(chatAdapter);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mDb = AppDatabase.getInstance(this);
        viewModel = ViewModelProviders.of(this, new ChatbotViewModelFactory(mDb)).get(ChatbotViewModel.class);
        viewModel.getChats().observe(this, chats ->{
            chatAdapter.setChats(chats);
            if(chatAdapter.getItemCount() > 1) {
                recyclerViewChats.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

        Intent intent = new Intent(this, ChatService.class);
        intent.setAction("SETUP");
        startService(intent);

        if(sharedPref.getBoolean("FIRST_RUN", true)){
            addInitChats();
        }
    }

    private void addInitChats(){
        String[] iniChats = {
                "Hello, i am FUT Bot",
                "I am Designed to help you with any information relating Federal University of Technology Minna",
                "Feel free to ask me, i am always available for you"
        };
        for(String initChat : iniChats) {
            Answer answer = new Answer();
            answer.setBody(initChat);
            answer.setExtras(new ArrayList<>());

            Message message = new Message();
            message.setType(Message.MessageType.ANSWER);
            message.setAnswer(answer);
            message.setTime(new Date());

            Chat chat = new Chat();
            chat.setType(Chat.ChatType.BOT);
            chat.setData(Constants.GSON.toJson(message));
            chat.setTime(new Date());
            chat.setSent(true);
            AppExecutors.getInstance().diskIO().execute(() -> {
                Chat lastChat = mDb.chatDao().getLast();
                if(lastChat == null || !Constants.DATE_FORMAT.format(lastChat.getTime()).equals(Constants.DATE_FORMAT.format(new Date()))){
                    Chat dateChat = new Chat();
                    dateChat.setTime(new Date());
                    dateChat.setType(Chat.ChatType.DATE);
                    dateChat.setData(Constants.GSON.toJson(new Date()));
                    mDb.chatDao().addChat(dateChat);
                }
                mDb.chatDao().addChat(chat);
            });
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("FIRST_RUN", false);
        editor.apply();
    }

    private void sendChat(){
        String text = edtAskText.getText().toString().trim();

        if(!text.isEmpty()) {
            chatService.ask(text);
            edtAskText.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(edtAskText.getWindowToken(), 0);
            }
        }
    }

    private void startSpeechToText(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.prompt_make_speech));
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtra("phone", sharedPref.getString(getString(R.string.prompt_phone), null));
        intent.putExtra("code", sharedPref.getString(getString(R.string.prompt_code), null));
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                ChatService.MessagingBinder binder = (ChatService.MessagingBinder) service;
                chatService = binder.getService();
                isBound = true;
                chatService.setAppRunning(isRunning);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                isBound = false;
            }
        };
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(chatService != null){
            chatService.setAppRunning(false);
        }
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        if(chatService != null){
            chatService.setAppRunning(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chatbot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_delete){
            List<Chat> chats = new ArrayList<>();
            for(int i = 0; i< chatAdapter.getSelection().length; i++){
                if(chatAdapter.getSelection()[i]){
                    chats.add(chatAdapter.getChats().get(i));
                }
            }
            chatAdapter.setSelectionCount(0);
            onSelectionCountChanged(0);
            AppExecutors.getInstance().diskIO().execute(() -> mDb.chatDao().deleteAllChats(chats));
            Toast.makeText(this, chats.size() + " item(s) Deleted !!!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isSelectionMode){
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_settings).setVisible(false);
        }else{
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_settings).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSelectionCountChanged(int selectionCount) {
        if(selectionCount == 0) {
            isSelectionMode = false;
            selectionToolbar.setVisibility(View.GONE);
            mainToolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(mainToolbar);
        }else{
            isSelectionMode = true;
            selectionToolbar.setVisibility(View.VISIBLE);
            mainToolbar.setVisibility(View.GONE);
            setSupportActionBar(selectionToolbar);

            TextView txtSelectionCount = selectionToolbar.findViewById(R.id.txt_selection_count);
            txtSelectionCount.setText(getResources().getQuantityString(R.plurals.format_selection_count, selectionCount, selectionCount));
            selectionToolbar.findViewById(R.id.img_btn_back).setOnClickListener(view -> {
                chatAdapter.clearSelections();
                chatAdapter.setSelectionCount(0);
                onSelectionCountChanged(0);
            });
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onVote(Chat chat, int position) {
        String phone = sharedPref.getString(getString(R.string.prompt_phone), null);
        String code = sharedPref.getString(getString(R.string.prompt_code), null);

        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);
        Poll poll = message.getPoll();
        PollItem pollItem = poll.getItems().get(position);

        Call<Boolean> resultCall = viewModel.getPollClient().sendVote(Constants.KEY, phone, code, pollItem.getId());
        resultCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if((response.isSuccessful() && response.body() != null && response.body())){
                    poll.setVoted(pollItem);
                    message.setPoll(poll);
                    chat.setData(Constants.GSON.toJson(message));
                    AppExecutors.getInstance().diskIO().execute(() -> mDb.chatDao().updateChat(chat));
                }else{
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("POLL VOTE", t.getMessage(), t);
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onExtraClicked(Extra extra) {
        Intent intent;
        switch(extra.getType()){
            case IMAGE:
            case LINK:
            case AUDIO:
            case VIDEO:
            case LOCATION:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(extra.getData()));
                break;
            case PHONE:
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+234" + extra.getData().substring(1)));
                break;
            case WHATSAPP:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/:+234" + extra.getData().substring(1)));
                break;
            case EMAIL:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + extra.getData()));
                intent.putExtra(Intent.EXTRA_EMAIL, extra.getData());
                break;
            default:
                return;
        }
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH_RECOGNIZER) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(results != null && !results.isEmpty()){
                    edtAskText.setText(results.get(0));
                }
            }
        }
    }
}