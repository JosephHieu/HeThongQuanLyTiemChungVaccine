package com.josephhieu.vaccinebackend.modules.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationEntryPoint;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationFilter;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.FinanceSummaryResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.VaccineFullResponse;
import com.josephhieu.vaccinebackend.modules.finance.service.FinanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FinanceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private FinanceService financeService;

    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @DisplayName("GET /summary - Thành công trả về code 1000")
    void getSummary_Success() throws Exception {
        FinanceSummaryResponse mockSummary = FinanceSummaryResponse.builder()
                .totalRevenueToday(new BigDecimal("1500000"))
                .build();

        when(financeService.getFinanceSummary()).thenReturn(mockSummary);

        mockMvc.perform(get("/api/v1/finance/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.totalRevenueToday").value(1500000));
    }

    @Test
    @DisplayName("GET /vaccines - Kiểm tra phân trang data[0]")
    void getVaccines_Pagination_Success() throws Exception {
        VaccineFullResponse v1 = VaccineFullResponse.builder().tenVacXin("Moderna").build();
        PageResponse<VaccineFullResponse> mockPage = PageResponse.<VaccineFullResponse>builder()
                .currentPage(1)
                .pageSize(7)
                .totalElements(1)
                .data(List.of(v1))
                .build();

        when(financeService.getVaccineManagementList(anyInt(), anyInt(), any())).thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/finance/vaccines")
                        .param("page", "1")
                        .param("size", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data[0].tenVacXin").value("Moderna"));
    }

    @Test
    @DisplayName("POST /confirm - Thành công")
    void confirmPayment_Success() throws Exception {
        UUID maHoaDon = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/finance/transactions/customers/{maHoaDon}/confirm", maHoaDon)
                        .param("phuongThuc", "Thẻ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));
    }
}