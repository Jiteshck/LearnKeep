package com.example.learnkeep.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────────
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ── Signup OTP Flow ──────────────────────────────────────────────
    @POST("send-signup-otp")
    Call<Map<String, Object>> sendSignupOtp(@Body Map<String, String> body);

    @POST("signup")
    Call<LoginResponse> signupWithOtp(@Body OtpSignupRequest request);

    // ── Forgot Password OTP Flow ─────────────────────────────────────
    @POST("forgot-password-otp")
    Call<Map<String, Object>> sendForgotOtp(@Body Map<String, String> body);

    @POST("reset-password")
    Call<Map<String, Object>> resetPassword(@Body Map<String, String> body);

    // ── Profile Picture (base64 encoded, saved to MongoDB) ───────────
    @PUT("update-profile-pic")
    Call<Map<String, Object>> updateProfilePic(@Body Map<String, String> body);

    @GET("profile")
    Call<ProfileResponse> getProfile();
}
