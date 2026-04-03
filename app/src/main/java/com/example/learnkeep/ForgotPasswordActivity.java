package com.example.learnkeep;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnkeep.api.ApiClient;
import com.example.learnkeep.api.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail, etOtp, etNewPassword;
    Button btnSendOtp, btnReset;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnReset = findViewById(R.id.btnReset);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Send OTP
        btnSendOtp.setOnClickListener(v -> sendOtp());

        // Reset Password
        btnReset.setOnClickListener(v -> resetPassword());
    }

    private void sendOtp() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        apiService.sendForgotOtp(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Toast.makeText(ForgotPasswordActivity.this, "OTP sent to email", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("otp", otp);
        body.put("newPassword", newPassword);

        apiService.resetPassword(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Toast.makeText(ForgotPasswordActivity.this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}