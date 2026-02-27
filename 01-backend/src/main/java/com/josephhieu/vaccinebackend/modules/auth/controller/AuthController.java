package com.josephhieu.vaccinebackend.modules.auth.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.LoginRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.modules.auth.service.AuthService;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller chịu trách nhiệm quản lý quy trình xác thực và định danh (Authentication & Identity).
 * <p>
 * Cung cấp các điểm cuối (endpoints) cho việc ghi danh người dùng mới và thiết lập
 * phiên làm việc thông qua cơ chế cấp phát mã thông báo (Token-based Authentication).
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Tiếp nhận yêu cầu đăng ký tài khoản mới cho bệnh nhân hoặc người dùng hệ thống.
     * <p>
     * Quy trình bao gồm việc kiểm tra tính hợp lệ của thông tin, mã hóa mật khẩu
     * và khởi tạo hồ sơ người dùng cơ bản.
     * </p>
     *
     * @param request Chứa thông tin đăng ký (Username, Password, Email...).
     * @return {@link ResponseEntity} với mã 201 (Created) và thông tin tài khoản vừa khởi tạo.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Khởi tạo tiến trình đăng ký tài khoản cho định danh: {}", request.getTenDangNhap());
        UserResponse result = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Đăng ký tài khoản thành công."));
    }

    /**
     * Thực hiện xác thực danh tính người dùng và thiết lập phiên làm việc.
     * <p>
     * Khi xác thực thành công, hệ thống trả về thông tin người dùng kèm theo quyền hạn (Roles)
     * và mã truy cập để thực hiện các yêu cầu tiếp theo.
     * </p>
     *
     * @param request Thông tin định danh (Username/Password).
     * @return {@link ResponseEntity} chứa thông tin định danh và quyền hạn của người dùng.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody @Valid LoginRequest request) {
        log.info("Xử lý yêu cầu đăng nhập cho tài khoản: {}", request.getTenDangNhap());
        UserResponse result = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success(result, "Đăng nhập hệ thống thành công."));
    }
}