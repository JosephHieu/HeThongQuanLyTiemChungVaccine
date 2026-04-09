package com.josephhieu.vaccinebackend.modules.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.config.SecurityConfig;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineExportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineImportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.InventoryResponse;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.VaccineExportResponse;
import com.josephhieu.vaccinebackend.modules.inventory.service.InventoryService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryService inventoryService;

    // Cấu hình Mock cho Security
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private VaccineImportRequest importRequest;
    private InventoryResponse inventoryResponse;

    @BeforeEach
    void setUp() {
        importRequest = VaccineImportRequest.builder()
                .tenVacXin("AstraZeneca")
                .maLoaiVacXin(UUID.randomUUID())
                .soLo("BATCH-123")
                .soLuong(100)
                .giaNhap(new BigDecimal("100000"))
                .donGia(new BigDecimal("150000"))
                .maNhaCungCap(UUID.randomUUID())
                .ngayNhan(LocalDate.now())
                .hanSuDung(LocalDate.now().plusYears(1))
                .build();

        inventoryResponse = InventoryResponse.builder()
                .maLo(UUID.randomUUID())
                .soLo("BATCH-123")
                .tenVacXin("AstraZeneca")
                .soLuong(100)
                .build();
    }

    @Test
    @WithMockUser(authorities = "Quản lý kho")
    @DisplayName("POST /import: Thành công khi Quản lý kho nhập hàng")
    void importVaccine_Success() throws Exception {
        when(inventoryService.importVaccine(any())).thenReturn(inventoryResponse);

        mockMvc.perform(post("/api/v1/inventory/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(importRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.soLo").value("BATCH-123"))
                .andExpect(jsonPath("$.message").value("Nhập kho vắc-xin mới thành công"));
    }

    @Test
    @WithMockUser(authorities = "Bệnh nhân")
    @DisplayName("POST /import: Bị chặn (403) vì Bệnh nhân không có quyền nhập kho")
    void importVaccine_Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(importRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "Quản lý kho")
    @DisplayName("POST /export: Thành công khi xuất kho điều phối")
    void exportVaccine_Success() throws Exception {
        VaccineExportResponse exportResponse = VaccineExportResponse.builder()
                .soPhieuXuat("PX-999")
                .soLuongDaXuat(10)
                .ngayXuat(LocalDateTime.now())
                .build();

        when(inventoryService.exportVaccine(any())).thenReturn(exportResponse);

        VaccineExportRequest exportRequest = VaccineExportRequest.builder()
                .maLo(UUID.randomUUID())
                .soLuongXuat(10)
                .noiNhan("Phòng tiêm A")
                .build();

        mockMvc.perform(post("/api/v1/inventory/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exportRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.soPhieuXuat").value("PX-999"))
                .andExpect(jsonPath("$.message").value("Xuất kho thành công! Số phiếu: PX-999"));
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("GET /stats/total-doses: Admin lấy tổng liều thành công")
    void getTotalDoses_Success() throws Exception {
        when(inventoryService.getTotalDoses()).thenReturn(5000L);

        mockMvc.perform(get("/api/v1/inventory/stats/total-doses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(5000));
    }

    @Test
    @WithMockUser(authorities = "Quản lý kho")
    @DisplayName("GET /export-history: Kiểm tra format ngày tháng trong lịch sử")
    void getExportHistory_Success() throws Exception {
        // Test kiểm tra việc gửi param ngày tháng có gây lỗi format không
        mockMvc.perform(get("/api/v1/inventory/export-history")
                        .param("startDate", "2026-01-01T08:00:00")
                        .param("endDate", "2026-12-31T17:00:00"))
                .andExpect(status().isOk());
    }
}