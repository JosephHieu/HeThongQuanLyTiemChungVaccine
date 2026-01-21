package com.josephhieu.vaccinebackend.modules.auth.controller;

import com.josephhieu.vaccinebackend.modules.auth.dto.request.LoginRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các tiến trình xác thực hệ thống.
 * Bao gồm các chức năng: Đăng ký tài khoản và Đăng nhập.
 * * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * API tiếp nhận yêu cầu đăng ký tài khoản mới.
     * Dữ liệu được kiểm tra hợp lệ trước khi chuyển xuống tầng Service.
     *
     * @param request Thông tin đăng ký từ người dùng.
     * @return ApiResponse chứa thông tin tài khoản sau khi tạo thành công.
     */
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody @Valid RegisterRequest request) {

        log.info("Nhận yêu cầu đăng ký cho user: {}", request.getTenDangNhap());

        return ApiResponse.<UserResponse>builder()
                .message("Đăng ký tài khoản thành công.")
                .result(authService.register(request))
                .build();
    }

    /**
     * API tiếp nhận yêu cầu đăng nhập hệ thống.
     * Trả về thông tin người dùng kèm danh sách quyền (Roles) để phân quyền Actor. [cite: 72]
     *
     * @param request Thông tin đăng nhập (Username/Password).
     * @return ApiResponse chứa thông tin định danh và quyền hạn của người dùng.
     */
    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@RequestBody @Valid LoginRequest request) {

        log.info("Nhận yêu cầu đăng nhập từ user: {}", request.getTenDangNhap());

        UserResponse result = authService.login(request);

        return ApiResponse.<UserResponse>builder()
                .message("Đăng nhập thành công.")
                .result(result)
                .build();
    }
}
