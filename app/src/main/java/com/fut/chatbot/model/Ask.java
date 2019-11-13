/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import java.util.Date;

public class Ask {

    public enum AskAccuracy {
        UNKNOWN, RIGHT, WRONG
    };

    private int id;
    private String text;
    private Question question;
    private User user;
    private Date askTime;

    private AskAccuracy accuracy;

    public Ask() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public AskAccuracy getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(AskAccuracy accuracy) {
        this.accuracy = accuracy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getAskTime() {
        return askTime;
    }

    public void setAskTime(Date askTime) {
        this.askTime = askTime;
    }
}
