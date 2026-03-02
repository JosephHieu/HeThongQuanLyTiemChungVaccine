package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.config.SecurityConfig;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.PrescribeRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.MedicalRecordResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.PatientProfileResponse;
import com.josephhieu.vaccinebackend.modules.medical.service.MedicalRecordService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicalController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class MedicalControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private MedicalRecordService medicalRecordService;

    // Mocks cho Security
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // --- TEST PHÂN HỆ NHÂN VIÊN ---

    @Test
    @WithMockUser(authorities = "Nhân viên y tế")
    @DisplayName("GET /records/{id}: Nhân viên y tế truy cập hồ sơ thành công")
    void getRecord_Success() throws Exception {
        UUID patientId = UUID.randomUUID();
        MedicalRecordResponse response = MedicalRecordResponse.builder()
                .id(patientId)
                .hoTen("Nguyễn Văn A")
                .build();

        when(medicalRecordService.getMedicalRecord(patientId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/medical/records/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.hoTen").value("Nguyễn Văn A"));
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("GET /records/{id}: Bệnh nhân không được phép xem hồ sơ của người khác (403)")
    void getRecord_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/medical/records/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "Nhân viên y tế")
    @DisplayName("POST /confirm-injection: Xác nhận tiêm thành công")
    void confirmInjection_Success() throws Exception {
        UUID maDangKy = UUID.randomUUID();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("phanUngSauTiem", "Bình thường");
        requestBody.put("thoiGianTacDung", "6 tháng");

        mockMvc.perform(post("/api/v1/medical/records/confirm-injection/{maDangKy}", maDangKy)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value("Xác nhận hoàn thành mũi tiêm và tạo hồ sơ bệnh án thành công."));
    }

    // --- TEST PHÂN HỆ BỆNH NHÂN ---

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("GET /my-profile: Bệnh nhân xem hồ sơ cá nhân thành công")
    void getMyProfile_Success() throws Exception {
        PatientProfileResponse profile = PatientProfileResponse.builder()
                .hoTen("Joseph Hieu")
                .soDienThoai("0909123456")
                .build();

        when(medicalRecordService.getMyProfile()).thenReturn(profile);

        mockMvc.perform(get("/api/v1/medical/my-profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.hoTen").value("Joseph Hieu"));
    }

    @Test
    @WithMockUser(authorities = "Nhân viên y tế")
    @DisplayName("GET /my-profile: Nhân viên không có quyền truy cập profile cá nhân (403)")
    void getMyProfile_Forbidden() throws Exception {
        // Lưu ý: Tùy vào thiết kế Role, nếu Nhân viên cũng là Bệnh nhân thì test này sẽ khác.
        // Ở đây test dựa trên authority 'Normal User Account' mà bạn đặt trong Controller.
        mockMvc.perform(get("/api/v1/medical/my-profile"))
                .andExpect(status().isForbidden());
    }
}