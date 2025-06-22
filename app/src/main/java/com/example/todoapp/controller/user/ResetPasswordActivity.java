package com.example.todoapp.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.model.ApiResponse;
import com.example.todoapp.model.ResetPasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText newPassword, confirmPassword;
    private Button resetPasswordButton;
    private TextView loginRedirectText;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
    }

    private void setupClickListeners() {
        resetPasswordButton.setOnClickListener(v -> resetPassword());

        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void resetPassword() {
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        resetPasswordButton.setEnabled(false);
        resetPasswordButton.setText("Resetting...");

        ResetPasswordRequest request = new ResetPasswordRequest(email, newPass, otp);

        RetrofitClient.getApiService().resetPassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                resetPasswordButton.setEnabled(true);
                resetPasswordButton.setText("Reset Password");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ResetPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                resetPasswordButton.setEnabled(true);
                resetPasswordButton.setText("Reset Password");
                Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}