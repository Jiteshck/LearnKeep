package com.example.learnkeep.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("signup")
    Call<LoginResponse> signup(@Body SignupRequest request);
    @POST("forgot-password-otp")
    Call<Map<String, Object>> sendForgotOtp(@Body Map<String, String> body);

    @POST("reset-password")
    Call<Map<String, Object>> resetPassword(@Body Map<String, String> body);
}