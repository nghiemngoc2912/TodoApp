package com.example.todoapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.controller.task.TaskList;
import com.example.todoapp.controller.user.GetProfileActivity;
import com.example.todoapp.controller.user.LoginActivity;
import com.example.todoapp.controller.user.TaskTrackingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private TextView welcomeText;
    private Button logoutButton;
    protected BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();
        setupBottomNavigation();
    }
    protected void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav == null) return;

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_task) {
                    startActivity(new Intent(this, TaskList.class));
                return true;
            } else if (id == R.id.nav_tracking) {
                    startActivity(new Intent(this, TaskTrackingActivity.class));
                return true;
            } else
            if (id == R.id.nav_profile) {
                if (!getClass().equals(GetProfileActivity.class)) {
                    startActivity(new Intent(this, GetProfileActivity.class));
                }
                return true;
            }
            return false;
        });
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