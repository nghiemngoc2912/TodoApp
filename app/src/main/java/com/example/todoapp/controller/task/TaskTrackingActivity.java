package com.example.todoapp.controller.task;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiService;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.controller.BaseMenuBottomActivity;
import com.example.todoapp.controller.user.LoginActivity;
import com.example.todoapp.model.TaskCountByDate;
import com.example.todoapp.model.TaskStartedCountByDateDTO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskTrackingActivity extends BaseMenuBottomActivity {

    TextView tvCompletedTasks, tvOpenTasks;
    BarChart barChartTaskStarted;
    LineChart lineChartDailyStatus;
    String token=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_tracking);

        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvOpenTasks = findViewById(R.id.tvOpenTasks);
        lineChartDailyStatus = findViewById(R.id.lineChartDailyStatus);
        barChartTaskStarted = findViewById(R.id.barChartTaskStarted);
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        token = prefs.getString("token", null);
        if (token == null) {
            // Chuyển hướng về Login nếu không có token
            startActivity(new Intent(TaskTrackingActivity.this, LoginActivity.class));
            finish();
            return;
        }
        fetchTaskCounts();
        fetchDailyTaskStatus();
        fetchDailyTaskStarted();
    }

    private void fetchTaskCounts() {
        // Gọi API count-completed-task
        ApiService api = RetrofitClient.getRetrofitInstance().create(ApiService.class); // Tùy tên client của bạn

        api.countCompletedTask("Bearer "+token).enqueue(new Callback<Integer>() {

            @Override public void onResponse(Call<Integer> call, Response<Integer> response) {
                tvCompletedTasks.setText("Completed\n" + response.body());
            }
            @Override public void onFailure(Call<Integer> call, Throwable t) {}
        });

        // Gọi API count-open-task
        api.countOpenTask("Bearer "+token).enqueue(new Callback<Integer>() {
            @Override public void onResponse(Call<Integer> call, Response<Integer> response) {
                tvOpenTasks.setText("Open\n" + response.body());
            }
            @Override public void onFailure(Call<Integer> call, Throwable t) {}
        });
    }



    private void fetchDailyTaskStatus() {
        ApiService api = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        api.getDailyStatus("Bearer " + token, 7).enqueue(new Callback<List<TaskCountByDate>>() {
            @Override
            public void onResponse(Call<List<TaskCountByDate>> call, Response<List<TaskCountByDate>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API", "Response failed or body is null");
                    return;
                }

                List<TaskCountByDate> data = response.body();

                if (data.isEmpty()) {
                    Log.w("API", "No task data returned");
                    return;
                }

                List<Entry> completedEntries = new ArrayList<>();
                List<Entry> openEntries = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                int i = 0;
                for (TaskCountByDate d : data) {
                    labels.add(d.getDate());
                    completedEntries.add(new Entry(i, d.getDoneCount()));
                    openEntries.add(new Entry(i, d.getInProgressCount()));
                    i++;
                }

                // Chart setup...
                LineDataSet completedSet = new LineDataSet(completedEntries, "Completed");
                completedSet.setColor(Color.parseColor("#4CAF50"));
                completedSet.setCircleColor(Color.parseColor("#4CAF50"));
                completedSet.setLineWidth(2f);
                completedSet.setCircleRadius(4f);
                completedSet.setDrawValues(true);

                LineDataSet openSet = new LineDataSet(openEntries, "Open");
                openSet.setColor(Color.parseColor("#F44336"));
                openSet.setCircleColor(Color.parseColor("#F44336"));
                openSet.setLineWidth(2f);
                openSet.setCircleRadius(4f);
                openSet.setDrawValues(true);

                LineData lineData = new LineData(completedSet, openSet);
                lineChartDailyStatus.setData(lineData);

                XAxis xAxis = lineChartDailyStatus.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                xAxis.setLabelRotationAngle(-45f);

                YAxis leftAxis = lineChartDailyStatus.getAxisLeft();
                leftAxis.setDrawGridLines(true);
                leftAxis.setAxisMinimum(0f);
                lineChartDailyStatus.getAxisRight().setEnabled(false);

                lineChartDailyStatus.getDescription().setEnabled(false);
                lineChartDailyStatus.getLegend().setEnabled(true);
                lineChartDailyStatus.animateY(1000);
                lineChartDailyStatus.invalidate();
                lineChartDailyStatus.setExtraOffsets(0f, 0f, 0f, 32f);
            }


            @Override
            public void onFailure(Call<List<TaskCountByDate>> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchDailyTaskStarted() {
        ApiService api = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        api.getTasksCreatedDaily("Bearer " + token, 7).enqueue(new Callback<List<TaskStartedCountByDateDTO>>() {
            @Override
            public void onResponse(Call<List<TaskStartedCountByDateDTO>> call, Response<List<TaskStartedCountByDateDTO>> response) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null) {
                    int i = 0;
                    for (TaskStartedCountByDateDTO d : response.body()) {
                        entries.add(new BarEntry(i, d.getstartedCount()));
                        String date = d.getDate();
                        labels.add(date);
                        i++;
                    }
                } else {
                    Log.e("API", "Response failed or empty");
                    return;
                }

                BarDataSet dataSet = new BarDataSet(entries, "Tasks Started");
                dataSet.setColor(Color.MAGENTA);
                BarData data = new BarData(dataSet);
                data.setBarWidth(0.5f);

                barChartTaskStarted.setData(data);

                XAxis xAxis = barChartTaskStarted.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                xAxis.setLabelRotationAngle(-45f); // Xoay nhãn trục X

                barChartTaskStarted.getAxisLeft().setAxisMinimum(0f); // Tùy chọn
                barChartTaskStarted.getAxisRight().setEnabled(false);
                barChartTaskStarted.getDescription().setEnabled(false);
                barChartTaskStarted.setExtraOffsets(0f, 0f, 0f, 32f);
                barChartTaskStarted.animateY(1000);
                barChartTaskStarted.invalidate();
            }

            @Override
            public void onFailure(Call<List<TaskStartedCountByDateDTO>> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

}
