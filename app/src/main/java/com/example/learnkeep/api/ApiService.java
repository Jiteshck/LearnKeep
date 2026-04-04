package com.example.learnkeep.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // ── Auth ─────────────────────────────────────────────────────────

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ── Signup OTP Flow ──────────────────────────────────────────────

    /** Step 1: send 6-digit OTP to email before creating account */
    @POST("send-signup-otp")
    Call<Map<String, Object>> sendSignupOtp(@Body Map<String, String> body);

    /** Step 2: verify OTP + create the account */
    @POST("signup")
    Call<LoginResponse> signupWithOtp(@Body OtpSignupRequest request);

    // ── Forgot Password OTP Flow ─────────────────────────────────────

    /** Step 1: send OTP to email for password reset */
    @POST("forgot-password-otp")
    Call<Map<String, Object>> sendForgotOtp(@Body Map<String, String> body);

    /** Step 2: verify OTP + set new password */
    @POST("reset-password")
    Call<Map<String, Object>> resetPassword(@Body Map<String, String> body);
}
