/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

public class UserPreference {

    private int id;
    private User user;
    private PreferenceValue preferenceValue;

    public UserPreference() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PreferenceValue getPreference() {
        return preferenceValue;
    }

    public void setPreference(PreferenceValue preferenceValue) {
        this.preferenceValue = preferenceValue;
    }


}
