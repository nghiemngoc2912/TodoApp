package com.example.todoapp.api;

import com.example.todoapp.model.ApiResponseDTO;
import com.example.todoapp.model.ChangeEmailRequestDTO;
import com.example.todoapp.model.ChangePasswordRequestDTO;
import com.example.todoapp.model.ForgotPasswordRequestDTO;
import com.example.todoapp.model.LoginRequestDTO;
import com.example.todoapp.model.ResetPasswordRequestDTO;
import com.example.todoapp.model.SignupRequestDTO;
import com.example.todoapp.model.UserProfileResponseDTO;
import com.example.todoapp.model.VerifyOTPRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<ApiResponseDTO> login(@Body LoginRequestDTO request);

    @POST("api/auth/signup")
    Call<ApiResponseDTO> signup(@Body SignupRequestDTO request);

    @POST("api/auth/forgot-password")
    Call<ApiResponseDTO> forgotPassword(@Body ForgotPasswordRequestDTO request);

    @POST("api/auth/verify-otp")
    Call<ApiResponseDTO> verifyOTP(@Body VerifyOTPRequestDTO request);

    @POST("api/auth/reset-password")
    Call<ApiResponseDTO> resetPassword(@Body ResetPasswordRequestDTO request);
    @GET("api/users/profile")
    Call<UserProfileResponseDTO> getProfile(@Header("Authorization") String token);
    @POST("api/users/change-email")
    Call<ApiResponseDTO> changeEmail(@Body ChangeEmailRequestDTO request);
    @POST("api/users/change-password")
    Call<ApiResponseDTO> changePassword(@Body ChangePasswordRequestDTO request);
}
