package com.josephhieu.vaccinebackend.controller;

import com.josephhieu.vaccinebackend.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller xử lý các nghiệp vụ liên quan đến tài khoản người dùng.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * API Tạo nhân viên mới.
     * @Valid đảm bảo các ràng buộc như MISSING_INFO được kiểm tra.
     */
    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .result(userService.createNewUser(request))
                .build();
    }

    /**
     * API Lấy danh sách tài khoản hỗ trợ phân trang.
     * @param page Số trang hiện tại (mặc định là 1).
     * @param size Số bản ghi mỗi trang (mặc định là 10).
     */
    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(1000)
                .result(userService.getAllUsers(page, size)) // Gọi service đã xử lý PageResponse
                .build();
    }

    // Api Sửa thông tin
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable UUID id, @RequestBody @Valid UserCreationRequest request) {

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(id, request))
                .build();
    }

    // API Khóa/ Mở khóa
    @PatchMapping("/{id}/toggle-status")
    public ApiResponse<String> toggleStatus(@PathVariable UUID id) {

        userService.toggleLock(id);
        return ApiResponse.<String>builder()
                .result("Cập nhật trạng thái thành công")
                .build();
    }
}