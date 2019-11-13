package com.fut.chatbot.database;

import androidx.room.TypeConverter;

public class ChatTypeConverter {
    @TypeConverter
    public static Chat.ChatType toChatType(int type) {
        return Chat.ChatType.values()[type];
    }

    @TypeConverter
    public static int toInt(Chat.ChatType type) {
        return type.ordinal();
    }
}
