package com.josephhieu.vaccinebackend.modules.identity.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.RoleResponse;
import com.josephhieu.vaccinebackend.modules.identity.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller quản lý danh mục vai trò (Roles) trong hệ thống.
 * <p>
 * Cung cấp các giao diện để truy xuất thông tin về các nhóm quyền hạn,
 * phục vụ cho cơ chế phân quyền dựa trên vai trò (RBAC - Role-Based Access Control).
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
@Slf4j
public class RoleController {

    private final RoleService roleService;

    /**
     * Truy xuất toàn bộ danh sách các vai trò hiện có trong hệ thống.
     * <p>
     * Dữ liệu này thường được sử dụng tại màn hình Quản lý tài khoản
     * để gán quyền cho nhân viên hoặc người dùng mới.
     * </p>
     *
     * @return {@link ResponseEntity} chứa danh sách các đối tượng {@link RoleResponse}.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getRoles() {
        log.info("Yêu cầu truy xuất danh sách toàn bộ vai trò hệ thống.");
        List<RoleResponse> result = roleService.getAllRoles();

        return ResponseEntity.ok(ApiResponse.success(result, "Tải danh sách vai trò thành công."));
    }
}