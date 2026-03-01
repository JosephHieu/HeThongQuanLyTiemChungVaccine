package com.josephhieu.vaccinebackend.modules.finance.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.VaccineFullResponse;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoaiVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class FinanceServiceImplTest {

    @Mock private VacXinRepository vacXinRepository;
    @Mock private LoaiVacXinRepository loaiVacXinRepository;
    @Mock private LoVacXinRepository loVacXinRepository;
    @Mock private HoaDonRepository hoaDonRepository;

    @InjectMocks
    private FinanceServiceImpl financeService;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private UUID mockId;
    private VacXin mockVacXin;
    private LoaiVacXin mockLoai;

    @BeforeEach
    void setUp() {
        mockId = UUID.randomUUID();
        mockLoai = LoaiVacXin.builder().MaLoaiVacXin(UUID.randomUUID()).tenLoaiVacXin("Vắc-xin sống").build();
        mockVacXin = VacXin.builder()
                .maVacXin(mockId)
                .tenVacXin("VNVC - AstraZeneca")
                .loaiVacXin(mockLoai)
                .donGia(new BigDecimal("250000"))
                .build();

        // Cấu hình Mock Security Context để lấy "System Admin"
        SecurityContextHolder.setContext(securityContext);
    }

    // --- TEST PHÂN HỆ 1: QUẢN LÝ VẮC-XIN ---

    @Test
    @DisplayName("createVaccine: Thành công khi loại vắc-xin tồn tại")
    void createVaccine_Success() {
        // GIVEN
        VaccineFullRequest request = VaccineFullRequest.builder()
                .tenVacXin("Test Vaccine")
                .maLoaiVacXin(mockLoai.getMaLoaiVacXin())
                .donGia(new BigDecimal("100000"))
                .build();

        when(loaiVacXinRepository.findById(any())).thenReturn(Optional.of(mockLoai));
        when(vacXinRepository.save(any(VacXin.class))).thenReturn(mockVacXin);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("AdminTest");

        // WHEN
        VaccineFullResponse response = financeService.createVaccine(request);

        // THEN
        assertNotNull(response);
        assertEquals(mockVacXin.getTenVacXin(), response.getTenVacXin());
        verify(vacXinRepository, times(1)).save(any());
    }

    // --- TEST PHÂN HỆ 2: GIAO DỊCH KHÁCH HÀNG ---

    @Test
    @DisplayName("confirmPayment: Thất bại khi hóa đơn đã bị hủy (Trạng thái = 2)")
    void confirmPayment_Fail_InvoiceCancelled() {
        // GIVEN
        HoaDon cancelledInvoice = HoaDon.builder()
                .maHoaDon(mockId)
                .loaiHoaDon("XUAT")
                .trangThai(2) // Đã hủy
                .build();

        when(hoaDonRepository.findById(mockId)).thenReturn(Optional.of(cancelledInvoice));

        // WHEN & THEN
        AppException exception = assertThrows(AppException.class,
                () -> financeService.confirmPayment(mockId, "Tiền mặt"));

        assertEquals(ErrorCode.INVOICE_CANCELLED, exception.getErrorCode());
        verify(hoaDonRepository, never()).save(any());
    }

    @Test
    @DisplayName("confirmPayment: Thành công cập nhật trạng thái đã thanh toán")
    void confirmPayment_Success() {
        // GIVEN
        HoaDon pendingInvoice = HoaDon.builder()
                .maHoaDon(mockId)
                .loaiHoaDon("XUAT")
                .trangThai(0) // Chờ thanh toán
                .build();

        when(hoaDonRepository.findById(mockId)).thenReturn(Optional.of(pendingInvoice));

        // WHEN
        financeService.confirmPayment(mockId, "Chuyển khoản");

        // THEN
        assertEquals(1, pendingInvoice.getTrangThai());
        assertEquals("Chuyển khoản", pendingInvoice.getPhuongThucThanhToan());
        verify(hoaDonRepository, times(1)).save(pendingInvoice);
    }

    // --- TEST PHÂN HỆ 4: TỔNG QUAN ---

    @Test
    @DisplayName("getFinanceSummary: Trả về 0 khi database chưa có doanh thu")
    void getFinanceSummary_EmptyData() {
        // GIVEN
        when(hoaDonRepository.sumRevenueByPeriod(any(), any())).thenReturn(null);
        when(hoaDonRepository.countByTrangThaiAndLoaiHoaDon(0, "XUAT")).thenReturn(0L);
        when(loVacXinRepository.getTotalInventoryValue()).thenReturn(null);

        // WHEN
        var summary = financeService.getFinanceSummary();

        // THEN
        assertEquals(BigDecimal.ZERO, summary.getTotalRevenueToday());
        assertEquals(BigDecimal.ZERO, summary.getInventoryValue());
    }
}