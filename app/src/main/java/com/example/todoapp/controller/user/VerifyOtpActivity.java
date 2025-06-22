package com.example.todoapp.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.R;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.model.ApiResponse;
import com.example.todoapp.model.VerifyOTPRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button verifyOtpButton;
    private String email;
    private EditText[] otpFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        email = getIntent().getStringExtra("email");

        initViews();
        setupOtpInput();
        setupClickListeners();
    }

    private void initViews() {
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        verifyOtpButton = findViewById(R.id.verify_otp_button);

        otpFields = new EditText[]{otp1, otp2, otp3, otp4, otp5, otp6};
    }

    private void setupOtpInput() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (index < otpFields.length - 1) {
                            otpFields[index + 1].requestFocus();
                        }
                    } else if (s.length() == 0) {
                        if (index > 0) {
                            otpFields[index - 1].requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupClickListeners() {
        verifyOtpButton.setOnClickListener(v -> verifyOTP());
    }

    private void verifyOTP() {
        StringBuilder otpBuilder = new StringBuilder();
        for (EditText field : otpFields) {
            String digit = field.getText().toString().trim();
            if (digit.isEmpty()) {
                Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            otpBuilder.append(digit);
        }

        String otp = otpBuilder.toString();

        verifyOtpButton.setEnabled(false);
        verifyOtpButton.setText("Verifying...");

        VerifyOTPRequest request = new VerifyOTPRequest(email, otp);

        RetrofitClient.getApiService().verifyOTP(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                verifyOtpButton.setEnabled(true);
                verifyOtpButton.setText("Verify OTP");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(VerifyOtpActivity.this, "OTP verified successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("otp", otp);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                verifyOtpButton.setEnabled(true);
                verifyOtpButton.setText("Verify OTP");
                Toast.makeText(VerifyOtpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}