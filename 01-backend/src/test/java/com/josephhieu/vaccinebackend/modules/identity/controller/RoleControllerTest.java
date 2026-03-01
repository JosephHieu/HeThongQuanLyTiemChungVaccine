package com.josephhieu.vaccinebackend.modules.identity.controller;

import com.josephhieu.vaccinebackend.config.SecurityConfig;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.RoleResponse;
import com.josephhieu.vaccinebackend.modules.identity.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cập nhật RoleControllerTest:
 * 1. Sử dụng @MockitoBean (Spring Boot 3.4+).
 * 2. @Import(SecurityConfig.class) để đảm bảo phân quyền 403 hoạt động đúng.
 */
@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class) // QUAN TRỌNG: Nạp cấu hình bảo mật thật vào Test context
@AutoConfigureMockMvc(addFilters = true)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    // --- CÁC MOCK CẦN THIẾT CHO SECURITY CONFIG ---
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("GET /api/v1/roles: Thành công (200) khi truy cập bằng quyền Administrator")
    void getRoles_Success() throws Exception {
        // GIVEN
        RoleResponse role = RoleResponse.builder()
                .maQuyen(UUID.randomUUID())
                .tenQuyen("Administrator")
                .build();
        when(roleService.getAllRoles()).thenReturn(List.of(role));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result[0].tenQuyen").value("Administrator"));
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("GET /api/v1/roles: Bị chặn (403 Forbidden) do không có quyền Administrator")
    void getRoles_Forbidden() throws Exception {
        // Với SecurityConfig đã được Import, "Normal User Account" sẽ bị chặn ngay tại Filter
        mockMvc.perform(get("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}