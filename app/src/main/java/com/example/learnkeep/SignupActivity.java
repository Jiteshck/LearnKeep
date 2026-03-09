package com.example.learnkeep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learnkeep.api.ApiClient;
import com.example.learnkeep.api.ApiService;
import com.example.learnkeep.api.LoginResponse;
import com.example.learnkeep.api.SignupRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> signupUser());
    }

    private void signupUser(){

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        ApiService apiService =
                ApiClient.getClient(this).create(ApiService.class);

        SignupRequest request =
                new SignupRequest(name,email,password);

        apiService.signup(request).enqueue(
                new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {

                        if(response.isSuccessful()
                                && response.body()!=null
                                && response.body().success){

                            Toast.makeText(
                                    SignupActivity.this,
                                    "Account Created",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(
                                    new Intent(
                                            SignupActivity.this,
                                            LoginActivity.class
                                    )
                            );

                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {

                        Toast.makeText(
                                SignupActivity.this,
                                "Server Error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
