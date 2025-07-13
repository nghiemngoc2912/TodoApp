package com.example.todoapp.controller.task;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.databinding.ItemTaskBinding;
import com.example.todoapp.enumconstant.Status;
import com.example.todoapp.model.TaskResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskRowAdapter
        extends ListAdapter<TaskResponseDTO, TaskRowAdapter.TaskVH> {

    public interface RowListener {
        void onToggle(TaskResponseDTO t, boolean checked);
        void onClick(TaskResponseDTO t);
    }

    private final RowListener listener;

    public TaskRowAdapter(RowListener l) { super(DIFF); this.listener = l; }

    private static final DiffUtil.ItemCallback<TaskResponseDTO> DIFF =
            new DiffUtil.ItemCallback<>() {
                public boolean areItemsTheSame(@NonNull TaskResponseDTO o, @NonNull TaskResponseDTO n){
                    return o.getId()==n.getId();
                }
                public boolean areContentsTheSame(@NonNull TaskResponseDTO o,@NonNull TaskResponseDTO n){
                    return o.equals(n);
                }
            };

    static class TaskVH extends RecyclerView.ViewHolder{
        ItemTaskBinding b; TaskVH(ItemTaskBinding b){ super(b.getRoot()); this.b=b; }
    }

    @NonNull @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup p, int v){
        return new TaskVH(ItemTaskBinding.inflate(
                LayoutInflater.from(p.getContext()),p,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH h,int pos){
        TaskResponseDTO t = getItem(pos);
        ItemTaskBinding b = h.b;

        /* ------------- hiển thị tiêu đề & strike-through ------------- */
        b.tvTitle.setText(t.getTitle());
        boolean done = t.getStatus() == Status.Done;
        b.cbDone.setChecked(done);
        b.tvTitle.setPaintFlags(done
                ? b.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : b.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

        /* ------------- hiển thị giờ / ngày ------------- */
        // startTime trên server là ISO: "2025-07-13T14:30:00"
        LocalDateTime dt = LocalDateTime.parse(
                t.getStartTime(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        LocalDate today = LocalDate.now();
        String timeStr;
        if (dt.toLocalDate().isEqual(today)) {
            // cùng ngày hôm nay -> hiển thị giờ:phút
            timeStr = dt.toLocalTime().format(
                    DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            // task quá khứ / tương lai -> hiển thị dd/MM hoặc dd/MM/yyyy
            DateTimeFormatter fmt = dt.getYear() == today.getYear()
                    ? DateTimeFormatter.ofPattern("dd MMM")      // 07 Jul
                    : DateTimeFormatter.ofPattern("dd MMM yyyy"); // 07 Jul 2024
            timeStr = dt.format(fmt);
        }
        b.tvTime.setText(timeStr);

        h.b.cbDone.setOnCheckedChangeListener((btn,ck)->{
            if(btn.isPressed()) listener.onToggle(t,ck);
        });

        h.b.getRoot().setOnClickListener(v -> {
            boolean newChecked = !b.cbDone.isChecked();
            b.cbDone.setChecked(newChecked);          // update UI
            listener.onToggle(t, newChecked);         // callback lên Activity
        });
       // h.b.getRoot().setOnClickListener(v-> listener.onClick(t));
    }
}
