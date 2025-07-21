package com.example.todoapp.controller.task;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiService;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.controller.BaseMenuBottomActivity;
import com.example.todoapp.databinding.ActivityTaskDetailBinding;
import com.example.todoapp.model.TaskResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends BaseMenuBottomActivity {

    private ActivityTaskDetailBinding binding;
    private TaskResponseDTO task;
    private int taskId;
    private ApiService api;
    private String bearer;
    private static final DateTimeFormatter OUTPUT_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = RetrofitClient.getApiService();
        bearer = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", null);

        taskId = getIntent().getIntExtra("taskid", -1);
        if (taskId == -1) {
            Toast.makeText(this, "No task id!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTaskDetail();

        // Confirm before delete
        binding.btnDelete.setOnClickListener(v -> {
            if (task != null) {
                showConfirmDeleteDialog(task);
            }
        });

        binding.btnUpdate.setOnClickListener(v -> {
            if (task != null) {
                Intent intent = new Intent(this, UpdateTaskActivity.class);
                intent.putExtra("taskid", task.getId());
                startActivity(intent);
            }
        });
    }

    private void showConfirmDeleteDialog(TaskResponseDTO task) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_message, task.getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    dialog.dismiss();
                    deleteTask(task.getId());
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadTaskDetail() {
        api.getTaskById(bearer, taskId).enqueue(new Callback<TaskResponseDTO>() {
            @Override
            public void onResponse(Call<TaskResponseDTO> call, Response<TaskResponseDTO> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    task = resp.body();
                    displayTaskInfo();
                } else {
                    Toast.makeText(TaskDetailActivity.this,
                            "Load failed: " + resp.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TaskResponseDTO> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayTaskInfo() {
        binding.tvTitle.setText(task.getTitle());
        binding.tvDescription.setText(task.getDescription());

        // Format time (ISO -> nice)
        try {
            binding.tvTime.setText(LocalDateTime.parse(task.getStartTime()).format(OUTPUT_FMT));
        } catch (Exception e) {
            binding.tvTime.setText(task.getStartTime()); // fallback raw
        }

        binding.tvPriority.setText("Priority: " + task.getPriority().name());
    }

    private void deleteTask(int taskId) {
        api.deleteTask(bearer, taskId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TaskDetailActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // để màn trước refresh nếu muốn
                    finish();
                } else {
                    Toast.makeText(TaskDetailActivity.this,
                            "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskDetail();
    }
}
