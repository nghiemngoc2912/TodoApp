package com.example.todoapp.model;

import java.time.LocalDate;

public class TaskStartedCountByDateDTO {
    private String date;
    private long startedCount;

    public TaskStartedCountByDateDTO() {
    }

    public TaskStartedCountByDateDTO(String date, long startedCount) {
        this.date = date;
        this.startedCount = startedCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getstartedCount() {
        return startedCount;
    }

    public void setstartedCount(long startedCount) {
        this.startedCount = startedCount;
    }
}
