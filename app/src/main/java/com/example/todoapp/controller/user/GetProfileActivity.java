package com.example.todoapp.controller.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiService;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.model.UserProfileResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetProfileActivity extends AppCompatActivity {
    TextView username,email;
    Button changeEmail,changePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initViews();
        fetchUserProfile();
        setupClickListeners();
    }
    private void fetchUserProfile() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            // Chuyển hướng về Login nếu không có token
            startActivity(new Intent(GetProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<UserProfileResponseDTO> call = apiService.getProfile("Bearer " + token);

        call.enqueue(new Callback<UserProfileResponseDTO>() {
            @Override
            public void onResponse(Call<UserProfileResponseDTO> call, Response<UserProfileResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponseDTO profile = response.body();
                    username.setText(profile.getUsername());
                    email.setText(profile.getEmail());
                } else {
                    Toast.makeText(GetProfileActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponseDTO> call, Throwable t) {
                Toast.makeText(GetProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickListeners() {
        changeEmail.setOnClickListener(v -> {
            Intent intent = new Intent(GetProfileActivity.this,ChangeEmailActivity.class);
            startActivity(intent);
        });
        changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(GetProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        changeEmail=findViewById(R.id.buttonChangeEmail);
        changePassword=findViewById(R.id.buttonChangePassword);
    }
}
