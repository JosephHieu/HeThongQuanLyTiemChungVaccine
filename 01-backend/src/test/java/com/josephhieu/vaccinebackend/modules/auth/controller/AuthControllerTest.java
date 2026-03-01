package com.josephhieu.vaccinebackend.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.ForgotPasswordRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.LoginRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.ResetPasswordRequest;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationFilter;
import com.josephhieu.vaccinebackend.modules.auth.service.AuthService;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Tắt Security Filter để test logic Controller
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // Cần Mock các Bean phụ thuộc trong SecurityConfig để Context khởi tạo được
    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private UserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        mockUserResponse = UserResponse.builder()
                .tenDangNhap("josephhieu")
                .hoTen("Joseph Hieu")
                .roles(Set.of("Normal User Account"))
                .token("mock-jwt-token")
                .build();
    }

    @Test
    @DisplayName("Login - Thành công trả về code 1000")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("josephhieu", "password123");

        when(authService.login(any())).thenReturn(mockUserResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.tenDangNhap").value("josephhieu"));
    }

    @Test
    @DisplayName("Forgot Password - Thành công trả về message thông báo")
    void forgotPassword_Success() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest("joseph@gmail.com");

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hướng dẫn đặt lại mật khẩu đã được gửi đến email của bạn."));
    }

    @Test
    @DisplayName("Reset Password - Thành công")
    void resetPassword_Success() throws Exception {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token("valid-token")
                .newPassword("NewPass123")
                .build();

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));
    }

    @Test
    @DisplayName("Validation - Thất bại khi để trống Email")
    void forgotPassword_ValidationError() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest(""); // Trống

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1011)); // Mã INVALID_INFO trong ErrorCode của bạn
    }
}