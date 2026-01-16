package com.josephhieu.vaccinebackend.controller;

import com.josephhieu.vaccinebackend.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.dto.response.RoleResponse;
import com.josephhieu.vaccinebackend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .code(1000)
                .result(roleService.getAllRoles())
                .build();
    }
}
