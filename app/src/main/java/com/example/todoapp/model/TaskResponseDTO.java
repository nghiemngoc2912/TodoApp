package com.example.todoapp.model;

import com.example.todoapp.enumconstant.Priority;
import com.example.todoapp.enumconstant.Status;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class TaskResponseDTO {
    private int id;
    private String title;
    private String description;
    @SerializedName("start_time")
    private String startTime;
    private Priority priority;
    private Status status;

    public TaskResponseDTO() {
    }

    public TaskResponseDTO(int id, String title, String description, String startTime, Priority priority, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.priority = priority;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskResponseDTO)) return false;
        TaskResponseDTO that = (TaskResponseDTO) o;
        return id == that.id &&
                status == that.status &&
                priority == that.priority &&
                title.equals(that.title) &&
                (description == null ? that.description == null
                        : description.equals(that.description));
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
