package com.fut.chatbot.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fut.chatbot.database.AppDatabase;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.retrofit.PollClient;
import com.fut.chatbot.retrofit.RetrofitClient;

import java.util.List;

public class ChatbotViewModel extends ViewModel {

    private LiveData<List<Chat>> chats;

    private PollClient pollClient;

    ChatbotViewModel(AppDatabase database) {
        chats = database.chatDao().getAll();

        pollClient = RetrofitClient.buildService(PollClient.class);
    }

    public LiveData<List<Chat>> getChats() {
        return chats;
    }

    public PollClient getPollClient() {
        return pollClient;
    }

}
