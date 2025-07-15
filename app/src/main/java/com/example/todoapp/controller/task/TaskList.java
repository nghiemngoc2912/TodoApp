package com.example.todoapp.controller.task;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.api.ApiService;
import com.example.todoapp.api.RetrofitClient;
import com.example.todoapp.enumconstant.Status;
import com.example.todoapp.model.TaskResponseDTO;
import com.example.todoapp.model.TaskUpdateRequestDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class TaskList extends AppCompatActivity {

    /* ---------- view & adapter ---------- */
    private RecyclerView rvSections;
    private ConcatAdapter concat;
    private final Map<String, TaskRowAdapter> map = new LinkedHashMap<>();

    /* ---------- network ---------- */
    private ApiService api;
    private String bearer;

    /* ============================================================= */
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_task_list);

        findViewById(R.id.btnAdd)
                .setOnClickListener(v ->
                        startActivity(new Intent(this, AddTaskActivity.class)));

        rvSections = findViewById(R.id.rvTaskSections);
        rvSections.setLayoutManager(new LinearLayoutManager(this));
        concat = new ConcatAdapter();
        rvSections.setAdapter(concat);

        /* section cố định */
        createSec("Today");
        createSec("Future");
        createSec("Last 7 days");
        createSec("Last 30 days");

        /* token + retrofit */
        String tk = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
        if (tk == null) {
            finish();
            return;
        }
        bearer = "Bearer " + tk;
        api = RetrofitClient.getApiService();

        fetchTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTasks();
    }

    /* ---------------- helper ---------------- */
    private void createSec(String title) {
        TaskRowAdapter row = new TaskRowAdapter(listener);
        map.put(title, row);
        concat.addAdapter(new SectionAdapter(title, row));
    }

    private final TaskRowAdapter.RowListener listener = new TaskRowAdapter.RowListener() {
        public void onToggle(TaskResponseDTO t, boolean ck) {
            update(t, ck);
        }

        public void onClick(TaskResponseDTO t) {
            Intent it = new Intent(TaskList.this, TaskDetailActivity.class);
            it.putExtra("taskid", t.getId());
            startActivity(it);
        }
    };

    /* ---------------- networking ---------------- */
    private void fetchTasks() {
        api.getAllTasks(bearer).enqueue(new Callback<>() {
            public void onResponse(Call<List<TaskResponseDTO>> c,
                                   Response<List<TaskResponseDTO>> r) {
                if (r.isSuccessful() && r.body() != null) distribute(r.body());
                else Toast.makeText(TaskList.this, "Load failed", Toast.LENGTH_SHORT).show();
            }

            public void onFailure(Call<List<TaskResponseDTO>> c, Throwable t) {
                Toast.makeText(TaskList.this, "Network error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /* ---------------- grouping & sorting ---------------- */
    /** sắp xếp: Inprogress trước, Done sau */
    private List<TaskResponseDTO> sortByDone(List<TaskResponseDTO> src) {
        return src.stream()
                .sorted(Comparator
                        .comparing((TaskResponseDTO t) -> t.getStatus() == Status.Done)
                        .thenComparing(TaskResponseDTO::getStartTime))
                .collect(Collectors.toList());
    }

    private void distribute(List<TaskResponseDTO> all) {
        map.values().forEach(a -> a.submitList(null));          // clear

        LocalDate today = LocalDate.now();
        int thisYear = today.getYear();

        List<TaskResponseDTO> secToday = new ArrayList<>();
        List<TaskResponseDTO> secFuture = new ArrayList<>();
        List<TaskResponseDTO> sec7 = new ArrayList<>();
        List<TaskResponseDTO> sec30 = new ArrayList<>();
        TreeMap<Month, List<TaskResponseDTO>> monthMap = new TreeMap<>();
        TreeMap<Integer, List<TaskResponseDTO>> yearMap = new TreeMap<>();

        DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (TaskResponseDTO t : all) {
            LocalDateTime dt = LocalDateTime.parse(t.getStartTime(), iso);
            LocalDate d = dt.toLocalDate();

            if (d.equals(today))          secToday.add(t);
            else if (d.isAfter(today))    secFuture.add(t);
            else {
                long diff = ChronoUnit.DAYS.between(d, today);
                if (diff <= 7)            sec7.add(t);
                else if (diff <= 30)      sec30.add(t);
                else if (d.getYear() == thisYear)
                    monthMap.computeIfAbsent(d.getMonth(), m -> new ArrayList<>()).add(t);
                else
                    yearMap.computeIfAbsent(d.getYear(), y -> new ArrayList<>()).add(t);
            }
        }

        /* nạp list có sort */
        map.get("Today").submitList(sortByDone(secToday));
        map.get("Future").submitList(sortByDone(secFuture));
        map.get("Last 7 days").submitList(sortByDone(sec7));
        map.get("Last 30 days").submitList(sortByDone(sec30));

        /* xoá section động cũ */
        for (int i = concat.getAdapters().size() - 1; i >= 0; i--) {
            RecyclerView.Adapter<?> ad = concat.getAdapters().get(i);
            if (ad instanceof SectionAdapter) {
                SectionAdapter sa = (SectionAdapter) ad;
                String title = sa.getTitle();
                if (title.matches("^(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).*")
                        || title.matches("^\\d{4}$")) {
                    concat.removeAdapter(ad);
                }
            }
        }

        /* tháng của năm hiện tại (mới → cũ) */
        for (Map.Entry<Month, List<TaskResponseDTO>> e : monthMap.descendingMap().entrySet()) {
            String title = e.getKey().getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    + " " + thisYear;
            addDynamicSection(title, sortByDone(e.getValue()));
        }
        /* năm cũ (mới → cũ) */
        for (Map.Entry<Integer, List<TaskResponseDTO>> e : yearMap.descendingMap().entrySet()) {
            addDynamicSection(String.valueOf(e.getKey()), sortByDone(e.getValue()));
        }
    }

    private void addDynamicSection(String title, List<TaskResponseDTO> list) {
        TaskRowAdapter row = new TaskRowAdapter(listener);
        row.submitList(list);
        concat.addAdapter(new SectionAdapter(title, row));
    }

    /* ---------------- update checkbox ---------------- */
    private void update(TaskResponseDTO oldT, boolean done){
        final TaskRowAdapter rowAd = concat.getAdapters().stream()
                .filter(ad -> ad instanceof SectionAdapter)
                .map(ad -> ((SectionAdapter)ad).getRowAd())
                .filter(ad -> ad.getCurrentList().contains(oldT))
                .findFirst().orElse(null);
        if(rowAd == null) return;

        int idx = rowAd.getCurrentList().indexOf(oldT);
        TaskResponseDTO newT = new TaskResponseDTO(
                oldT.getId(), oldT.getTitle(), oldT.getDescription(),
                oldT.getStartTime(), oldT.getPriority(),
                done ? Status.Done : Status.Inprogress);

        List<TaskResponseDTO> cur = new ArrayList<>(rowAd.getCurrentList());
        cur.set(idx, newT);
        rowAd.submitList(sortByDone(cur));

        TaskUpdateRequestDTO body = new TaskUpdateRequestDTO();
        body.setTitle(newT.getTitle());
        body.setDescription(newT.getDescription());
        body.setPriority(newT.getPriority());
        body.setStatus(newT.getStatus());
        body.setStartTime(newT.getStartTime());

        // ✅ Dùng rowAd và idx vì bây giờ chúng đã là final/effectively final
        api.updateTask(bearer, newT.getId(), body)
                .enqueue(new Callback<>() {
                    public void onResponse(Call<String> c, Response<String> r) {
                        if (!r.isSuccessful()) rollback(rowAd, idx, oldT);
                    }
                    public void onFailure(Call<String> c, Throwable t) {
                        rollback(rowAd, idx, oldT);
                    }
                });
    }
    private void rollback(TaskRowAdapter ad, int idx, TaskResponseDTO oldT){
        List<TaskResponseDTO> cur = new ArrayList<>(ad.getCurrentList());
        cur.set(idx, oldT);
        ad.submitList(sortByDone(cur));
        Toast.makeText(this, "Update failed – reverted", Toast.LENGTH_SHORT).show();
    }

}