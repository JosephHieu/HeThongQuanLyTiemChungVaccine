package com.josephhieu.vaccinebackend.modules.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.config.SecurityConfig;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // Các Mock Bean bắt buộc để SecurityConfig khởi động
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private UserCreationRequest validRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        validRequest = UserCreationRequest.builder()
                .tenDangNhap("hieu_dev_2026")
                .matKhau("password123")
                .maQuyen(UUID.randomUUID().toString())
                .hoTen("Joseph Hieu")
                .cmnd("123456789")
                .noiO("TP.HCM")
                .build();

        userResponse = UserResponse.builder()
                .maTaiKhoan(UUID.randomUUID())
                .tenDangNhap("hieu_dev_2026")
                .roles(Set.of("Administrator"))
                .build();
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("POST /create: Admin tạo tài khoản thành công")
    void createUser_Success() throws Exception {
        when(userService.createNewUser(any(UserCreationRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.tenDangNhap").value("hieu_dev_2026"));
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("POST /create: Lỗi 400 khi tên đăng nhập chứa ký tự đặc biệt")
    void createUser_InvalidUsername() throws Exception {
        validRequest.setTenDangNhap("hieu@123"); // Vi phạm @Pattern ^[a-zA-Z0-9_]+$

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("POST /create: Lỗi 400 khi CMND sai định dạng")
    void createUser_InvalidCMND() throws Exception {
        validRequest.setCmnd("ABC12345"); // Vi phạm @Pattern ^[0-9]{9,12}$

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("GET /api/v1/users: Người dùng thường bị chặn (403) khi xem danh sách")
    void getAllUsers_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("GET /api/v1/users: Admin lấy danh sách phân trang thành công")
    void getAllUsers_Success() throws Exception {
        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .currentPage(1)
                .pageSize(10)
                .totalElements(1)
                .data(List.of(userResponse))
                .build();

        when(userService.getAllUsers(anyInt(), anyInt(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data[0].tenDangNhap").value("hieu_dev_2026"));
    }
}