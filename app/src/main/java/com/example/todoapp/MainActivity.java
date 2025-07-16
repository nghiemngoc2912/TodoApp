package com.example.todoapp;

import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import com.example.todoapp.controller.BaseMenuBottomActivity;
import com.example.todoapp.controller.user.LoginActivity;

public class MainActivity extends BaseMenuBottomActivity {
    private TextView welcomeText;
    private Button logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();
    }
    private void initViews() {
        welcomeText = findViewById(R.id.welcome_text);
        logoutButton = findViewById(R.id.logout_button);
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().remove("token").apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}