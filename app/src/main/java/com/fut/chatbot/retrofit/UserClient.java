package com.fut.chatbot.retrofit;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserClient {

    @POST("activate-phone")
    Call<Result> activatePhone(@Header("key") String key, @Query("phone") String phone);

    @POST("activate-code")
    Call<Result> activateCode(@Header("key") String key, @Query("phone") String phone, @Query("code") String code);

    @POST("update-name")
    Call<Result> updateName(@Header("key") String key, @Query("phone") String phone, @Query("code") String code, @Query("newName") String newName);

    @POST("update-phone")
    Call<Result> updatePhone(@Header("key") String key, @Query("phone") String phone, @Query("code") String code, @Query("newPhone") String newPhone);
}
