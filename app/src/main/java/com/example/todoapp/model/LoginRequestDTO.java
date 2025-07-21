package com.example.todoapp.model;


public class LoginRequestDTO {

    private String username;
    private String password;

    private String idToken;

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginRequestDTO(String idToken) {
        this.idToken = idToken;
    }

    public LoginRequestDTO() {
    }

    // Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}
