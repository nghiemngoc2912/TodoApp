package com.example.todoapp.controller.task;

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
import com.example.todoapp.databinding.ActivityAddTaskBinding;
import com.example.todoapp.enumconstant.Priority;
import com.example.todoapp.enumconstant.Status;
import com.example.todoapp.model.TaskUpdateRequestDTO;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    private ActivityAddTaskBinding binding;
    private ApiService api;
    private String bearer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* ---- Retrofit + token ---- */
        bearer = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE)
                .getString("token", null);
        api = RetrofitClient.getApiService();

        setupPriorityDropdown();
        setupDateTimePicker();
        setupAddButton();
    }

    /* ---------------- Priority dropdown ---------------- */
    private void setupPriorityDropdown() {
        String[] items = {"High", "Medium", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, items);
        binding.autoPriority.setAdapter(adapter);
        binding.autoPriority.setText(items[0], false);   // default "High"
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

    /* ---------------- Add Task btn ---------------- */
    private void setupAddButton() {
        binding.btnAddTask.setOnClickListener(v -> {
            if (!validate()) return;

            TaskUpdateRequestDTO dto = new TaskUpdateRequestDTO();
            dto.setTitle(binding.edtTitle.getText().toString().trim());
            dto.setDescription(binding.edtDesc.getText().toString().trim());
            dto.setStartTime(binding.edtTime.getText().toString().trim());
            dto.setPriority(getPriorityEnum(binding.autoPriority.getText().toString()));
            dto.setStatus(Status.Inprogress);

            api.createTask(bearer, dto).enqueue(new Callback<String>() {
                @Override public void onResponse(Call<String> c, Response<String> r) {
                    if (r.isSuccessful()) {
                        Toast.makeText(AddTaskActivity.this,
                                "Task added!", Toast.LENGTH_SHORT).show();
                        finish();   // quay về danh sách
                    } else {
                        Toast.makeText(AddTaskActivity.this,
                                "Add failed: "+r.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<String> c, Throwable t) {
                    Toast.makeText(AddTaskActivity.this,
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