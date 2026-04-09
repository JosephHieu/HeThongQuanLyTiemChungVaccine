package com.josephhieu.vaccinebackend.modules.vaccination.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.config.SecurityConfig;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.service.VaccineService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VaccineController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class VaccineControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private VaccineService vaccineService;

    // Security Mocks để khởi chạy context
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @WithMockUser(authorities = "Nhân viên y tế")
    @DisplayName("POST /register: Nhân viên y tế bị chặn (403) khi cố đăng ký tiêm")
    void register_ForbiddenForStaff() throws Exception {
        mockMvc.perform(post("/api/v1/vaccinations/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("GET /my-registrations: Lấy lịch sử đăng ký thành công")
    void getMyRegistrations_Success() throws Exception {
        mockMvc.perform(get("/api/v1/vaccinations/my-registrations"))
                .andExpect(status().isOk());

        verify(vaccineService).getMyRegistrations();
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("POST /cancel/{id}: Hủy đăng ký thành công")
    void cancel_Success() throws Exception {
        UUID maDangKy = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/vaccinations/cancel/{maDangKy}", maDangKy))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Hủy đăng ký thành công và đã hoàn lại vắc-xin vào kho"));

        verify(vaccineService).cancelRegistration(maDangKy);
    }
}