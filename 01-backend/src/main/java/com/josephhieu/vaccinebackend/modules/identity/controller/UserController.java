package com.josephhieu.vaccinebackend.modules.identity.controller;

import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.StaffSummaryResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller xử lý các nghiệp vụ liên quan đến tài khoản người dùng.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String maQuyen
    ) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(1000)
                .result(userService.getAllUsers(page, size, search, maQuyen))
                .build();
    }

    // Api Sửa thông tin
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable UUID id, @RequestBody @Valid UserCreationRequest request) {

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(id, request))
                .build();
    }

    /**
     * API Lấy danh sách nhân viên y tế (Bác sĩ).
     * Dùng để đổ dữ liệu vào dropdown "Bác sĩ phụ trách" ở module Tiêm chủng.
     * Endpoint: GET /api/users/medical-staffs
     */
    @GetMapping("/medical-staffs")
    public ApiResponse<List<StaffSummaryResponse>> getMedicalStaffs() {
        log.info("Yêu cầu lấy danh sách nhân viên y tế để phân công lịch trực");

        return ApiResponse.<List<StaffSummaryResponse>>builder()
                .code(1000)
                .result(userService.getStaffsByRole("Nhân viên y tế"))
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