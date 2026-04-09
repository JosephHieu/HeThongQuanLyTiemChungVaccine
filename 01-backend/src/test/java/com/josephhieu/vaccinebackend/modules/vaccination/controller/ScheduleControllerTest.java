package com.josephhieu.vaccinebackend.modules.vaccination.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.config.SecurityConfig;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.ScheduleResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.ScheduleService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class ScheduleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ScheduleService scheduleService;

    // Security Mocks
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @WithMockUser(authorities = "Nhân viên y tế")
    @DisplayName("POST /schedules: Nhân viên tạo lịch tiêm thành công")
    void createSchedule_Success() throws Exception {
        ScheduleCreationRequest request = ScheduleCreationRequest.builder()
                .ngayTiem(LocalDate.now().plusDays(5))
                .thoiGian("Sáng")
                .diaDiem("Phòng 101")
                .build();

        ScheduleResponse response = ScheduleResponse.builder()
                .diaDiem("Phòng 101")
                .build();

        when(scheduleService.createScheduleService(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/vaccination/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.diaDiem").value("Phòng 101"));
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("POST /schedules: Bệnh nhân bị chặn khi cố tạo lịch tiêm (403)")
    void createSchedule_Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/vaccination/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("GET /opening: Bệnh nhân xem được các lịch đang mở đăng ký")
    void getOpeningSchedules_Success() throws Exception {
        ScheduleResponse schedule = ScheduleResponse.builder()
                .tenVacXin("AstraZeneca")
                .build();

        when(scheduleService.getOpeningSchedulesForUser()).thenReturn(List.of(schedule));

        mockMvc.perform(get("/api/v1/vaccination/schedules/opening"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].tenVacXin").value("AstraZeneca"));
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("GET /by-date: Admin tra cứu lịch theo ngày thành công")
    void getByDate_Success() throws Exception {
        LocalDate date = LocalDate.of(2026, 3, 10);

        mockMvc.perform(get("/api/v1/vaccination/schedules/by-date")
                        .param("date", "2026-03-10")
                        .param("shift", "Sáng"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("DELETE /{id}: Xóa lịch tiêm thành công")
    void deleteSchedule_Success() throws Exception {
        UUID scheduleId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/vaccination/schedules/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa lịch tiêm thành công"));
    }
}