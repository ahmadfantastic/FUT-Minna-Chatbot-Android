/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

public class Option {

    private int id;
    private String text;
    private Answer parentAnswer;
    private Answer childAnswer;
    private PreferenceValue preferenceValue;

    public Option() {
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

    public Answer getChildAnswer() {
        return childAnswer;
    }

    public void setChildAnswer(Answer childAnswer) {
        this.childAnswer = childAnswer;
    }

    public Answer getParentAnswer() {
        return parentAnswer;
    }

    public void setParentAnswer(Answer parentAnswer) {
        this.parentAnswer = parentAnswer;
    }

    public PreferenceValue getPreferenceValue() {
        return preferenceValue;
    }

    public void setPreferenceValue(PreferenceValue preferenceValue) {
        this.preferenceValue = preferenceValue;
    }

}
