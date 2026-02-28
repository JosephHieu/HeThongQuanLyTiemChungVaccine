package com.josephhieu.vaccinebackend.modules.auth.service;

import com.josephhieu.vaccinebackend.modules.auth.dto.request.LoginRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    UserResponse login(LoginRequest request);

    // Tính năng Quên mật khẩu
    void processForgotPassword(String email);
    void resetPassword(String token, String newPassword);
}