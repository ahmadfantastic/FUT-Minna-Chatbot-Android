package com.fut.chatbot.viewmodel;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fut.chatbot.database.AppDatabase;
import com.fut.chatbot.model.Ask;
import com.fut.chatbot.model.User;
import com.fut.chatbot.retrofit.RetrofitClient;
import com.fut.chatbot.retrofit.UserClient;

import java.util.List;

public class VerificationViewModel extends ViewModel {

    private User user;
    private CountDownTimer timer;

    private UserClient userClient;

    VerificationViewModel(AppDatabase database) {
        timer = null;

        userClient = RetrofitClient.buildService(UserClient.class);
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public CountDownTimer getTimer() {
        return timer;
    }

    public void setTimer(CountDownTimer timer) {
        this.timer = timer;
    }

    public UserClient getUserClient() {
        return userClient;
    }
}
