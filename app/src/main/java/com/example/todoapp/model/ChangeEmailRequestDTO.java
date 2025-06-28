package com.example.todoapp.model;

public class ChangeEmailRequestDTO {
    String password;
    String newEmail;
    public ChangeEmailRequestDTO() {
    }
    public ChangeEmailRequestDTO(String password, String newEmail) {
        this.password = password;
        this.newEmail = newEmail;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
