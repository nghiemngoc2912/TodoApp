package com.example.todoapp.controller.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiService;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.controller.BaseMenuBottomActivity;
import com.example.todoapp.controller.user.GetProfileActivity;
import com.example.todoapp.databinding.ActivityUpdateTaskBinding;
import com.example.todoapp.enumconstant.Priority;
import com.example.todoapp.enumconstant.Status;
import com.example.todoapp.model.TaskResponseDTO;
import com.example.todoapp.model.TaskUpdateRequestDTO;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTaskActivity extends BaseMenuBottomActivity {

    private ActivityUpdateTaskBinding binding;
    private ApiService api;
    private String bearer;
    private int taskid;
    private TaskResponseDTO task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        taskid =intent.getIntExtra("taskid",-1);

        /* ---- Retrofit + token ---- */
        bearer = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", null);
        api = RetrofitClient.getApiService();
        setupTaskDetail();
        setupPriorityDropdown();
        setupDateTimePicker();
        setupUpdateButton();
    }

    private void setupTaskDetail() {
        api.getTaskById(bearer, taskid).enqueue(new Callback<TaskResponseDTO>() {
            @Override
            public void onResponse(Call<TaskResponseDTO> call, Response<TaskResponseDTO> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    task = resp.body();
                    displayTaskInfo();
                } else {
                    Toast.makeText(UpdateTaskActivity.this,
                            "Load failed: " + resp.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TaskResponseDTO> call, Throwable t) {
                Toast.makeText(UpdateTaskActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayTaskInfo() {
        binding.edtTitle.setText(task.getTitle());
        binding.edtDesc.setText(task.getDescription());
        binding.edtTime.setText(task.getStartTime());
        binding.autoPriority.setText(task.getPriority().name(),false);
    }

    /* ---------------- Priority dropdown ---------------- */
    private void setupPriorityDropdown() {
        String[] items = {"High", "Medium", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, items);
        binding.autoPriority.setAdapter(adapter);
    }

    /* ---------------- Time picker ---------------- */
    private void setupDateTimePicker() {
        binding.edtTime.setOnClickListener(v -> openDatePicker());
    }

    private void openDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selection);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            openTimePicker(dateStr);
        });

        datePicker.show(getSupportFragmentManager(), "date_picker");
    }



    private void openTimePicker(String dateStr) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(18).setMinute(0)
                .setTitleText("Select time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int h = timePicker.getHour();
            int m = timePicker.getMinute();

            // ISO-8601 format: 2025-07-12T18:00:00
            String isoDateTime = String.format("%sT%02d:%02d:00", dateStr, h, m);

            binding.edtTime.setText(isoDateTime);   // hiển thị ISO
        });

        timePicker.show(getSupportFragmentManager(), "time_picker");
    }

    private void setupUpdateButton() {
        binding.btnUpdateTask.setOnClickListener(v -> {
            if (!validate()) return;

            TaskUpdateRequestDTO dto = new TaskUpdateRequestDTO();
            dto.setTitle(binding.edtTitle.getText().toString().trim());
            dto.setDescription(binding.edtDesc.getText().toString().trim());
            dto.setStartTime(binding.edtTime.getText().toString().trim());
            dto.setPriority(getPriorityEnum(binding.autoPriority.getText().toString()));
            dto.setStatus(task.getStatus());

            api.updateTask(bearer,taskid, dto).enqueue(new Callback<String>() {
                @Override public void onResponse(Call<String> c, Response<String> r) {
                    if (r.isSuccessful()) {
                        Toast.makeText(UpdateTaskActivity.this,
                                "Task updated!", Toast.LENGTH_SHORT).show();
                        finish();   // quay về danh sách
                    } else {
                        Toast.makeText(UpdateTaskActivity.this,
                                "Update failed: "+r.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<String> c, Throwable t) {
                    Toast.makeText(UpdateTaskActivity.this,
                            "Network error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /* ---------------- Helpers ---------------- */
    private boolean validate() {
        String title = binding.edtTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            binding.edtTitle.setError("Title required");
            return false;
        }
        if (TextUtils.isEmpty(binding.edtTime.getText().toString().trim())) {
            binding.edtTime.setError("Time required");
            return false;
        }
        return true;
    }

    private Priority getPriorityEnum(String text) {
        if ("High".equalsIgnoreCase(text))   return Priority.High;
        if ("Low".equalsIgnoreCase(text))    return Priority.Low;
        return Priority.Medium;
    }
}