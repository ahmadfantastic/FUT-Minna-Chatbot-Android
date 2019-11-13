package com.fut.chatbot.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChatDao {

    @Query("SELECT * FROM chat")
    LiveData<List<Chat>> getAll();

    @Query("SELECT * FROM chat WHERE type == :type AND sent == :sent")
    List<Chat> getAllByTypeAndSent(Chat.ChatType type, boolean sent);
    
    @Insert
    long addChat(Chat chat);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateChat(Chat chat);

    @Delete
    void deleteChat(Chat chat);

    @Delete
    void deleteAllChats(List<Chat> chats);

    @Query("SELECT * FROM chat WHERE id = :id")
    Chat getById(long id);

    @Query("SELECT * FROM chat ORDER BY id DESC LIMIT 1")
    Chat getLast();

    @Query("SELECT COUNT() FROM chat")
    int count();

}
