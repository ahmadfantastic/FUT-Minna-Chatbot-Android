/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.util.Constants;

public class Extra {

    public enum ExtraType {
        IMAGE, AUDIO, VIDEO, LOCATION, LINK, EMAIL, PHONE, WHATSAPP
    };

    private int id;
    private String data;
    private ExtraType type;
    private Answer answer;

    public Extra() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ExtraType getType() {
        return type;
    }

    public void setType(ExtraType type) {
        this.type = type;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

}
