package com.josephhieu.vaccinebackend.controller;

import com.josephhieu.vaccinebackend.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các yêu cầu HTTP liên quan đến Auth.
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * API Đăng ký tài khoản người dùng mới.
     */
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {

        return ApiResponse.<UserResponse>builder()
                .message("Đăng ký thành công tài khoản người dùng.")
                .result(authService.register(request))
                .build();
    }
}
