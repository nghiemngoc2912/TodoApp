package com.example.todoapp.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.model.ApiResponseDTO;
import com.example.todoapp.model.ForgotPasswordRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailInput;
    private Button sendOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.email_input);
        sendOtpButton = findViewById(R.id.send_otp_button);
    }

    private void setupClickListeners() {
        sendOtpButton.setOnClickListener(v -> sendOTP());
    }

    private void sendOTP() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        sendOtpButton.setEnabled(false);
        sendOtpButton.setText("Sending...");

        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO(email);

        RetrofitClient.getApiService().forgotPassword(request).enqueue(new Callback<ApiResponseDTO>() {
            @Override
            public void onResponse(Call<ApiResponseDTO> call, Response<ApiResponseDTO> response) {
                sendOtpButton.setEnabled(true);
                sendOtpButton.setText("Send OTP");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseDTO apiResponseDTO = response.body();
                    if (apiResponseDTO.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, "OTP sent to your email", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, apiResponseDTO.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDTO> call, Throwable t) {
                sendOtpButton.setEnabled(true);
                sendOtpButton.setText("Send OTP");
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}