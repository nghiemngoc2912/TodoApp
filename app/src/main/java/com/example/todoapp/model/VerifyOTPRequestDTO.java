package com.example.todoapp.model;

public class VerifyOTPRequestDTO {
    private String email;
    private String otp;

    public VerifyOTPRequestDTO(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
