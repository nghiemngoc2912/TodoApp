package com.example.todoapp.controller;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;
import com.example.todoapp.controller.task.TaskList;
import com.example.todoapp.controller.user.GetProfileActivity;
import com.example.todoapp.controller.task.TaskTrackingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseMenuBottomActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNav;

    @Override
    protected void onStart() {
        super.onStart();
        setupBottomNavigation();
    }

    protected void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav == null) return;

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_task) {
                if (!getClass().equals(TaskList.class)) {
                    startActivity(new Intent(this, TaskList.class));
                }
                return true;
            } else if (id == R.id.nav_tracking) {
                if (!getClass().equals(TaskTrackingActivity.class)) {
                    startActivity(new Intent(this, TaskTrackingActivity.class));
                }
                return true;
            } else if (id == R.id.nav_profile) {
                if (!getClass().equals(GetProfileActivity.class)) {
                    startActivity(new Intent(this, GetProfileActivity.class));
                }
                return true;
            }
            return false;
        });

        // Optional: set selected item based on current activity
        if (getClass().equals(TaskList.class)) {
            bottomNav.setSelectedItemId(R.id.nav_task);
        } else if (getClass().equals(TaskTrackingActivity.class)) {
            bottomNav.setSelectedItemId(R.id.nav_tracking);
        } else if (getClass().equals(GetProfileActivity.class)) {
            bottomNav.setSelectedItemId(R.id.nav_profile);
        }
    }
}

