package com.josephhieu.vaccinebackend.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.LoginRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.repository.PhanQuyenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PhanQuyenRepository phanQuyenRepository;

    @BeforeEach
    void setup() {
        if (phanQuyenRepository.findByTenQuyen("Normal User Account").isEmpty()) {
            phanQuyenRepository.save(PhanQuyen.builder()
                    .tenQuyen("Normal User Account")
                    .build());
        }
    }

    @Test
    @DisplayName("Luồng đầy đủ: Đăng ký thành công -> Đăng nhập thành công")
    void fullAuthFlow_Success() throws Exception {
        RegisterRequest regReq = RegisterRequest.builder()
                .tenDangNhap("joseph_test_" + System.currentTimeMillis())
                .matKhau("Admin@123")
                .email("test@gmail.com")
                .hoTen("Nguyễn Văn Test")
                .cmnd("012345678901") // Bắt buộc
                .noiO("123 Đường ABC, Quận 1, TP.HCM") // Bắt buộc
                .sdt("0909123456")
                .gioiTinh("Nam")
                .ngaySinh(LocalDate.of(1995, 5, 20))
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated()) // Mong đợi 201 Created
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.tenDangNhap").value(regReq.getTenDangNhap()));

        // 3. Giai đoạn Đăng nhập: Kiểm tra xem User vừa tạo có login được không
        LoginRequest loginReq = new LoginRequest(regReq.getTenDangNhap(), "Admin@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk()) // Mong đợi 200 OK
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.token").exists());
    }

    @Test
    @DisplayName("Đăng ký thất bại: Thiếu trường bắt buộc (Nơi ở)")
    void register_Fail_MissingRequiredField() throws Exception {
        RegisterRequest regReq = RegisterRequest.builder()
                .tenDangNhap("fail_user")
                .matKhau("password")
                .hoTen("No Address User")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isBadRequest()); // Mong đợi 400 Bad Request
    }
}