/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import java.util.List;

public class Preference {

    public enum PreferenceType {
        PREDEFINED, USER_DEFINED
    }

    private int id;
    private PreferenceType type;
    private String name;
    private List<PreferenceValue> values;

    public Preference() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PreferenceType getType() {
        return type;
    }

    public void setType(PreferenceType type) {
        this.type = type;
    }

    public List<PreferenceValue> getValues() {
        return values;
    }

    public void setValues(List<PreferenceValue> values) {
        this.values = values;
    }

}
