package com.fut.chatbot.retrofit;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PollClient {

    @POST("vote")
    Call<Boolean> sendVote(@Header("key") String key, @Header("phone") String phone,
                           @Header("code") String code, @Query("item") int pollItemId);

}
