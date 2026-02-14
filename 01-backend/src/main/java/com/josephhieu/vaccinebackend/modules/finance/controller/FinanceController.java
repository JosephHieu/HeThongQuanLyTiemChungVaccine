package com.josephhieu.vaccinebackend.modules.finance.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.*;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.service.FinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/finance") // 1. Chuyển về mapping chung của module
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    // =========================================================================
    // PHÂN HỆ 0: TỔNG QUAN (Dành cho các thẻ Summary ở Header)
    // =========================================================================

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<FinanceSummaryResponse> getSummary() {
        return ApiResponse.<FinanceSummaryResponse>builder()
                .result(financeService.getFinanceSummary())
                .build();
    }

    // Thống kê riêng cho Tab nhà cung cấp
    @GetMapping("/summary/suppliers")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<SupplierSummaryResponse> getSupplierSummary() {
        return ApiResponse.<SupplierSummaryResponse>builder()
                .result(financeService.getSupplierSummary())
                .build();
    }

    // =========================================================================
    // PHÂN HỆ 1: QUẢN LÝ DANH MỤC VẮC XIN (/api/finance/vaccines)
    // =========================================================================

    @GetMapping("/vaccines")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<PageResponse<VaccineFullResponse>> getVaccineList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String search) {
        return ApiResponse.<PageResponse<VaccineFullResponse>>builder()
                .result(financeService.getVaccineManagementList(page, size, search))
                .build();
    }

    @PostMapping("/vaccines")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<VaccineFullResponse> create(@RequestBody @Valid VaccineFullRequest request) {
        return ApiResponse.<VaccineFullResponse>builder()
                .message("Thêm mới vắc-xin thành công")
                .result(financeService.createVaccine(request))
                .build();
    }

    @PutMapping("/vaccines/{id}")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<VaccineFullResponse> update(@PathVariable UUID id, @RequestBody @Valid VaccineFullRequest request) {
        return ApiResponse.<VaccineFullResponse>builder()
                .message("Cập nhật vắc-xin thành công")
                .result(financeService.updateVaccine(id, request))
                .build();
    }

    @DeleteMapping("/vaccines/{id}")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        financeService.deleteVaccine(id);
        return ApiResponse.<Void>builder().message("Xóa thành công").build();
    }

    // =========================================================================
    // PHÂN HỆ 2: GIAO DỊCH KHÁCH HÀNG (/api/finance/transactions/customers)
    // =========================================================================

    @GetMapping("/transactions/customers")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<PageResponse<CustomerTransactionResponse>> getCustomerTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ApiResponse.<PageResponse<CustomerTransactionResponse>>builder()
                .result(financeService.getCustomerTransactions(page, size, search, startDate, endDate))
                .build();
    }

    @PostMapping("/transactions/customers/{maHoaDon}/confirm")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<Void> confirmPayment(@PathVariable UUID maHoaDon, @RequestParam String phuongThuc) {
        financeService.confirmPayment(maHoaDon, phuongThuc);
        return ApiResponse.<Void>builder().message("Xác nhận thanh toán thành công").build();
    }

    // =========================================================================
    // PHÂN HỆ 3: GIAO DỊCH NHÀ CUNG CẤP (CHI TIỀN)
    // =========================================================================

    @GetMapping("/transactions/suppliers")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<PageResponse<SupplierTransactionResponse>> getSupplierTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        // CẬP NHẬT: Trả về DTO SupplierTransactionResponse thay vì Entity HoaDon
        return ApiResponse.<PageResponse<SupplierTransactionResponse>>builder()
                .result(financeService.getSupplierTransactions(page, size, search))
                .build();
    }

    // MỚI: Lấy chi tiết đơn nhập hàng
    @GetMapping("/transactions/suppliers/{maHoaDon}")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<HoaDon> getSupplierDetail(@PathVariable UUID maHoaDon) {
        return ApiResponse.<HoaDon>builder()
                .result(financeService.getSupplierTransactionDetail(maHoaDon))
                .build();
    }

    // MỚI: Xác nhận đã trả tiền cho Nhà cung cấp
    @PostMapping("/transactions/suppliers/{maHoaDon}/confirm")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<Void> confirmSupplierPayment(
            @PathVariable UUID maHoaDon,
            @RequestParam String phuongThuc) {
        financeService.confirmSupplierPayment(maHoaDon, phuongThuc);
        return ApiResponse.<Void>builder().message("Xác nhận chi tiền cho NCC thành công").build();
    }

    @PostMapping("/transactions/{maHoaDon}/cancel")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<Void> cancelTransaction(@PathVariable UUID maHoaDon) {
        financeService.cancelTransaction(maHoaDon);
        return ApiResponse.<Void>builder().message("Đã hủy hóa đơn").build();
    }
}