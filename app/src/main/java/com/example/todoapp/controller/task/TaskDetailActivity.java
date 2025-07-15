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
import com.example.todoapp.databinding.ActivityTaskDetailBinding;
import com.example.todoapp.model.TaskResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends AppCompatActivity {

    private ActivityTaskDetailBinding binding;
    private TaskResponseDTO task;
    private int taskId;
    private ApiService api;
    private String bearer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        api = RetrofitClient.getApiService();
        bearer = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", null);

        // Nhận dữ liệu task từ Intent
        taskId = getIntent().getIntExtra("taskid", -1);
        if (taskId == -1) {
            Toast.makeText(this, "No task id!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTaskDetail();


        // Xử lý xoá
        binding.btnDelete.setOnClickListener(v -> deleteTask(task.getId()));

        // Xử lý cập nhật
        binding.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateTaskActivity.class);
            intent.putExtra("taskid", task.getId());
            startActivity(intent);
        });
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
                binding.tvTime.setText("Time: " + task.getStartTime());
                binding.tvPriority.setText("Priority: " + task.getPriority().name());
            }

            private void deleteTask(int taskId) {
                api.deleteTask(bearer, taskId).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(TaskDetailActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                            finish(); // Quay về màn trước
                        } else {
                            Toast.makeText(TaskDetailActivity.this, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(TaskDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskDetail();   // gọi lại mỗi khi Activity “trở lại”
    }

}