package com.example.todoapp.api;

import com.example.todoapp.model.ApiResponse;
import com.example.todoapp.model.ForgotPasswordRequest;
import com.example.todoapp.model.LoginRequest;
import com.example.todoapp.model.ResetPasswordRequest;
import com.example.todoapp.model.SignupRequest;
import com.example.todoapp.model.VerifyOTPRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<ApiResponse> login(@Body LoginRequest request);

    @POST("api/auth/signup")
    Call<ApiResponse> signup(@Body SignupRequest request);

    @POST("api/auth/forgot-password")
    Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/auth/verify-otp")
    Call<ApiResponse> verifyOTP(@Body VerifyOTPRequest request);

    @POST("api/auth/reset-password")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);
}
