package com.josephhieu.vaccinebackend.modules.support.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.config.SecurityConfig;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.support.dto.request.VaccinationReminderRequest;
import com.josephhieu.vaccinebackend.modules.support.dto.response.VaccinationReminderResponse;
import com.josephhieu.vaccinebackend.modules.support.service.VaccinationReminderService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VaccinationReminderController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class VaccinationReminderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private VaccinationReminderService reminderService;

    // Các thành phần giả lập Security Context
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @WithMockUser(authorities = "Hỗ trợ khách hàng")
    @DisplayName("GET /search: Nhân viên hỗ trợ tra cứu email thành công")
    void searchByEmail_Success() throws Exception {
        String email = "joseph.hieu@example.com";
        VaccinationReminderResponse mockResponse = VaccinationReminderResponse.builder()
                .hoTen("Joseph Hieu")
                .email(email)
                .build();

        when(reminderService.getPatientDataByEmail(email)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/support/reminders/search")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.hoTen").value("Joseph Hieu"));

        verify(reminderService).getPatientDataByEmail(email);
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("GET /search: Bệnh nhân không có quyền tra cứu (403 Forbidden)")
    void searchByEmail_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/support/reminders/search")
                        .param("email", "admin@example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("POST /send: Admin gửi email nhắc lịch thành công")
    void sendEmail_Success() throws Exception {
        VaccinationReminderRequest request = VaccinationReminderRequest.builder()
                .email("patient@example.com")
                .tieuDe("Nhắc lịch tiêm chủng mũi 2")
                .loiNhan("Vui lòng đến đúng hẹn")
                .build();

        // Service là void nên dùng doNothing()
        doNothing().when(reminderService).sendReminderEmail(any(VaccinationReminderRequest.class));

        mockMvc.perform(post("/api/v1/support/reminders/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email nhắc lịch đã được gửi thành công!"));

        verify(reminderService).sendReminderEmail(any(VaccinationReminderRequest.class));
    }
}