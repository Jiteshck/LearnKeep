package com.example.learnkeep;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnkeep.api.ApiClient;
import com.example.learnkeep.api.ApiService;
import com.example.learnkeep.api.LoginRequest;
import com.example.learnkeep.api.LoginResponse;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView txtSignup = findViewById(R.id.txtSignup);

        txtSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        findViewById(R.id.txtForgotPassword).setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser(){

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        LoginRequest request = new LoginRequest(email,password);
        apiService.login(request).enqueue(
                new retrofit2.Callback<LoginResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<LoginResponse> call,
                            retrofit2.Response<LoginResponse> response) {
                        if(response.isSuccessful()
                                && response.body()!=null
                                && response.body().success){

                            SessionManager session =
                                    new SessionManager(LoginActivity.this);
                            session.saveLogin(
                                    response.body().name,
                                    response.body().email,
                                    response.body().token
                            );
                            startActivity(new Intent(
                                    LoginActivity.this,
                                    MainActivity.class));

                            finish();
                        } else {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Invalid Login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(
                            retrofit2.Call<LoginResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Server Error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
