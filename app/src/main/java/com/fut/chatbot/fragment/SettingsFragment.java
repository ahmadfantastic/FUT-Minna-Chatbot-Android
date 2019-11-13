package com.fut.chatbot.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.fut.chatbot.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private final Context mContext;

    public SettingsFragment(Context context){
        mContext = context;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference ttsPreference = findPreference(getString(R.string.preference_key_tts_settings));
        if(ttsPreference != null){
            ttsPreference.setOnPreferenceClickListener(preference -> {
                Intent checkIntent = new Intent();
                checkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                checkIntent.setAction("com.android.settings.TTS_SETTINGS");
                mContext.startActivity(checkIntent);
                return false;
            });
        }
    }
}
