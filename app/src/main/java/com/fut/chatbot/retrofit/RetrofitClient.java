package com.fut.chatbot.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fut.chatbot.util.Constants.REST_BASE_URL;

//Retrofit instance is created here
public class RetrofitClient {

    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(REST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient);
    private static Retrofit retrofit = builder.build();

    //Service builder that creates implementation of those interfaces is created here.
    public static <T> T buildService(Class<T> type) {
        return retrofit.create(type);
    }
}