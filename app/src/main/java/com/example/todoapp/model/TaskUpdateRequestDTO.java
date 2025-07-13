package com.example.todoapp.model;

import androidx.annotation.NonNull;

import com.example.todoapp.enumconstant.Priority;
import com.example.todoapp.enumconstant.Status;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class TaskUpdateRequestDTO {

    @NonNull
    private String title;
    private String description;
    @SerializedName("start_time")
    private String startTime;
    private Priority priority;
    private Status status;

    public @NonNull String getTitle() {
        return title;
    }

    public void setTitle( @NonNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
