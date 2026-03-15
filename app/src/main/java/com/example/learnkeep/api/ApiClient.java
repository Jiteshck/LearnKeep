package com.example.learnkeep.api;

import android.content.Context;

import com.example.learnkeep.SessionManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            SessionManager session = new SessionManager(context);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(session))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://learnkeep.onrender.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}