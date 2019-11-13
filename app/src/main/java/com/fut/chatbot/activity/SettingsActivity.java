package com.fut.chatbot.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fut.chatbot.R;
import com.fut.chatbot.fragment.SettingsFragment;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.findViewById(R.id.img_btn_back).setOnClickListener(view -> finish());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_settings, new SettingsFragment(getApplicationContext()))
                .commit();
    }

}