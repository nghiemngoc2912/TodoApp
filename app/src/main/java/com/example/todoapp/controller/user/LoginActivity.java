package com.example.todoapp.controller.user;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.model.ApiResponse;
import com.example.todoapp.model.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView forgotPassword, signUpRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgot_password);
        signUpRedirectText = findViewById(R.id.signUpRedirectText);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> performLogin());

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signUpRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String username = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        LoginRequest request = new LoginRequest(username, password);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Save token
                        String token = (String) apiResponse.getData();
                        saveToken(token);

                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Navigate to main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
    }
}