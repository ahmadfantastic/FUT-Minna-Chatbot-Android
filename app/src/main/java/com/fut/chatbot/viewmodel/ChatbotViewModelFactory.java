package com.fut.chatbot.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fut.chatbot.database.AppDatabase;

public class ChatbotViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;

    public ChatbotViewModelFactory(AppDatabase database) {
        mDb = database;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new ChatbotViewModel(mDb);
    }
}
