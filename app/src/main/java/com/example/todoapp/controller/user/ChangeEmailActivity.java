package com.example.todoapp.controller.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

public class ChangeEmailActivity extends AppCompatActivity {
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
        });
    }

    private void initViews() {
        password=findViewById(R.id.password);
        email=findViewById(R.id.email);
        changeEmail=findViewById(R.id.buttonUpdateEmail);
    }
}
