package com.example.todoapp.controller.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiService;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.model.ApiResponseDTO;
import com.example.todoapp.model.ChangeEmailRequestDTO;
import com.example.todoapp.model.ChangePasswordRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    TextView oldPassword,newPassword,confirmPassword;
    Button changePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        setupClickListeners();
    }

    private void setupClickListeners() {
        changePassword.setOnClickListener(v -> {
            String oldPass = oldPassword.getText().toString().trim();
            String confirmPass = newPassword.getText().toString().trim();
            String newPass = newPassword.getText().toString().trim();

            if (oldPass.isEmpty() || confirmPass.isEmpty()||newPass.isEmpty()) {
                showError("Please fill all the fields");
                return;
            }
            if(!confirmPass.equals(newPass)){
                showError("Confirm password does not match");
                return;
            }
            ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(oldPass, newPass);
            ApiService api = RetrofitClient.getRetrofitInstance().create(ApiService.class); // Tùy tên client của bạn
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String token = prefs.getString("token", null);
            if (token == null) {
                // Chuyển hướng về Login nếu không có token
                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                finish();
                return;
            }
            api.changePassword("Bearer " + token,request).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        showError("Password update successfully", false); // Xanh
                    } else {
                        // Nếu server trả lỗi kèm body text
                        try {
                            String errorBody = response.errorBody().string();
                            showError("Error: " + errorBody);
                        } catch (Exception e) {
                            showError("Error happened.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    showError("Failed to connect server: " + t.getMessage());
                }
            });
        });
    }
    private void showError(String msg) {
        showError(msg, true);
    }

    private void showError(String msg, boolean isError) {
        TextView errorView = findViewById(R.id.textError); // Thêm 1 TextView dưới form trong XML
        errorView.setText(msg);
        errorView.setTextColor(getResources().getColor(isError ? android.R.color.holo_red_dark : android.R.color.holo_green_dark));
    }
    private void initViews() {
        oldPassword=findViewById(R.id.oldPassword);
        newPassword=findViewById(R.id.newPassword);
        confirmPassword=findViewById(R.id.confirmPassword);
        changePassword=findViewById(R.id.buttonChangePassword);
    }
}
