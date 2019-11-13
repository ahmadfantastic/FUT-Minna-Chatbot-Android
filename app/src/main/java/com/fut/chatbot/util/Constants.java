package com.fut.chatbot.util;

import android.annotation.SuppressLint;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public static final String KEY = "QWERTYUIOP";
    public static final Gson GSON = new GsonBuilder().create();
    private static final String PROTOCOL = "http";
    private static final String IP_ADDRESS = "192.168.43.149";
    private static final String DOMAIN = IP_ADDRESS + ":8080";
    public static final String IMAGE_BASE = PROTOCOL + "://" + IP_ADDRESS + "/chatbot/";
    public static final String REST_BASE_URL = PROTOCOL + "://" + DOMAIN + "/rest/";
    public static final String WEB_SOCKET_BASE_URL = "ws://" + DOMAIN + "/chatbot-web-socket/websocket";
    @SuppressLint("ConstantLocale")
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
    @SuppressLint("ConstantLocale")
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm", Locale.getDefault());
}
