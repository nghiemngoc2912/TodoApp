package com.example.todoapp.controller.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

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
        
        });
    }

    private void initViews() {
        oldPassword=findViewById(R.id.oldPassword);
        newPassword=findViewById(R.id.newPassword);
        confirmPassword=findViewById(R.id.confirmPassword);
        changePassword=findViewById(R.id.buttonChangePassword);
    }
}
