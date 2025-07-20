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
import com.example.todoapp.controller.BaseMenuBottomActivity;
import com.example.todoapp.model.ApiResponseDTO;
import com.example.todoapp.model.ChangeEmailRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailActivity extends BaseMenuBottomActivity {
    TextView password,email;
    Button changeEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        initViews();
        setupClickListeners();
    }
    private void setupClickListeners() {
        changeEmail.setOnClickListener(v -> {
            String pw = password.getText().toString().trim();
            String newEmailStr = email.getText().toString().trim();

            if (pw.isEmpty() || newEmailStr.isEmpty()) {
                showError("Please fill all the fields");
                return;
            }

            ChangeEmailRequestDTO request = new ChangeEmailRequestDTO(pw, newEmailStr);
            ApiService api = RetrofitClient.getRetrofitInstance().create(ApiService.class); // Tùy tên client của bạn
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String token = prefs.getString("token", null);
            if (token == null) {
                // Chuyển hướng về Login nếu không có token
                startActivity(new Intent(ChangeEmailActivity.this, LoginActivity.class));
                finish();
                return;
            }
            api.changeEmail("Bearer " + token,request).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        showError("Email update successfully", false); // Xanh
                    } else {
                        // Nếu server trả lỗi kèm body text
                        try {
                            String errorBody = response.errorBody().string();
                            showError(errorBody);
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
        password=findViewById(R.id.password);
        email=findViewById(R.id.email);

        changeEmail=findViewById(R.id.buttonUpdateEmail);
    }
}
