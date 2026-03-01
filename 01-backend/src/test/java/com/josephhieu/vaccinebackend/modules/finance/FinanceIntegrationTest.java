package com.josephhieu.vaccinebackend.modules.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoaiVacXinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FinanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private LoaiVacXinRepository loaiVacXinRepository;

    @BeforeEach
    void initData() {
        // Nạp sẵn dữ liệu loại vắc xin
        loaiVacXinRepository.save(LoaiVacXin.builder()
                .tenLoaiVacXin("Vắc-xin tái tổ hợp")
                .build());

        // Tạo sẵn một hóa đơn doanh thu (XUAT) đã thanh toán để test dashboard
        hoaDonRepository.save(HoaDon.builder()
                .tongTien(new BigDecimal("500000"))
                .loaiHoaDon("XUAT")
                .trangThai(1)
                .ngayTao(LocalDateTime.now())
                .build());

        // Tạo một hóa đơn NCC (NHAP) chưa thanh toán
        hoaDonRepository.save(HoaDon.builder()
                .tongTien(new BigDecimal("10000000"))
                .loaiHoaDon("NHAP")
                .trangThai(0)
                .ngayTao(LocalDateTime.now())
                .build());
    }

    @Test
    @WithMockUser(authorities = "Tài chính")
    @DisplayName("IT - Thống kê doanh thu hôm nay phải khớp với dữ liệu đã nạp")
    void getFinanceSummary_IntegrationSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/finance/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                // Kiểm tra xem SUM có tính ra 500000 không
                .andExpect(jsonPath("$.result.totalRevenueToday").value(500000.0));
    }

    @Test
    @WithMockUser(authorities = "Tài chính")
    @DisplayName("IT - Thống kê NCC phải thấy 1 hóa đơn đang nợ (NHAP, TrangThai 0)")
    void getSupplierSummary_IntegrationSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/finance/summary/suppliers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.overdueInvoices").value(1));
    }

    @Test
    @WithMockUser(authorities = "Normal User Account")
    @DisplayName("IT - Security chặn người dùng thường xem báo cáo tài chính")
    void getSummary_AccessDenied() throws Exception {
        mockMvc.perform(get("/api/v1/finance/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }
}