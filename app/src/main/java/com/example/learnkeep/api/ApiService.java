package com.example.learnkeep.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("signup")
    Call<LoginResponse> signup(@Body SignupRequest request);
}