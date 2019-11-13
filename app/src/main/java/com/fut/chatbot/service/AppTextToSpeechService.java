package com.fut.chatbot.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import com.squareup.seismic.ShakeDetector;

import java.util.Locale;

public class AppTextToSpeechService extends Service implements TextToSpeech.OnInitListener, ShakeDetector.Listener {
    private TextToSpeech tts;
    private ShakeDetector shakeDetector;

    private String text;
    private boolean isInit;

    @Override
    public void hearShake() {
        if(tts != null && tts.isSpeaking()) {
            tts.stop();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(getApplicationContext(), this);

        shakeDetector = new ShakeDetector(this);
        shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = intent.getStringExtra("text");

        if (isInit) {
            speak();
        }

        return AppTextToSpeechService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            shakeDetector.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                speak();
                isInit = true;
            }
        }
    }

    private void speak() {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, "message");
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
