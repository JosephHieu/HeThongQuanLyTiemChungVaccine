package com.josephhieu.vaccinebackend.modules.finance.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.*;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.service.FinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@Slf4j
public class FinanceController {

    private final FinanceService financeService;

    // =========================================================================
    // PHÂN HỆ 0: TỔNG QUAN (DASHBOARD ANALYTICS)
    // =========================================================================

    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<FinanceSummaryResponse>> getSummary() {
        log.info("Yêu cầu truy xuất báo cáo tổng quan tài chính hệ thống.");
        return ResponseEntity.ok(ApiResponse.success(financeService.getFinanceSummary()));
    }

    @GetMapping("/summary/suppliers")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')") // ĐÃ ĐỔI SANG FINANCE
    public ResponseEntity<ApiResponse<SupplierSummaryResponse>> getSupplierSummary() {
        log.info("Yêu cầu truy xuất tóm tắt giao dịch nhà cung cấp.");
        return ResponseEntity.ok(ApiResponse.success(financeService.getSupplierSummary()));
    }

    // =========================================================================
    // PHÂN HỆ 1: QUẢN LÝ DANH MỤC VẮC XIN & ĐỊNH GIÁ
    // =========================================================================

    @GetMapping("/vaccines")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<PageResponse<VaccineFullResponse>>> getVaccineList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(financeService.getVaccineManagementList(page, size, search)));
    }

    @PostMapping("/vaccines")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<VaccineFullResponse>> create(@RequestBody @Valid VaccineFullRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(financeService.createVaccine(request), "Thêm mới vắc-xin thành công"));
    }

    @PutMapping("/vaccines/{id}")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<VaccineFullResponse>> update(@PathVariable UUID id, @RequestBody @Valid VaccineFullRequest request) {
        return ResponseEntity.ok(ApiResponse.success(financeService.updateVaccine(id, request), "Cập nhật vắc-xin thành công"));
    }

    @DeleteMapping("/vaccines/{id}")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        financeService.deleteVaccine(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa vắc-xin thành công"));
    }

    // =========================================================================
    // PHÂN HỆ 2: GIAO DỊCH KHÁCH HÀNG (QUẢN LÝ DOANH THU)
    // =========================================================================

    @GetMapping("/transactions/customers")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<PageResponse<CustomerTransactionResponse>>> getCustomerTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(ApiResponse.success(financeService.getCustomerTransactions(page, size, search, startDate, endDate)));
    }

    @PostMapping("/transactions/customers/{maHoaDon}/confirm")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(@PathVariable UUID maHoaDon, @RequestParam String phuongThuc) {
        financeService.confirmPayment(maHoaDon, phuongThuc);
        return ResponseEntity.ok(ApiResponse.success(null, "Xác nhận thanh toán thành công"));
    }

    // =========================================================================
    // PHÂN HỆ 3: GIAO DỊCH NHÀ CUNG CẤP (QUẢN LÝ CHI PHÍ)
    // =========================================================================

    @GetMapping("/transactions/suppliers")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<PageResponse<SupplierTransactionResponse>>> getSupplierTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(financeService.getSupplierTransactions(page, size, search)));
    }

    @GetMapping("/transactions/suppliers/{maHoaDon}")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<HoaDon>> getSupplierDetail(@PathVariable UUID maHoaDon) {
        return ResponseEntity.ok(ApiResponse.success(financeService.getSupplierTransactionDetail(maHoaDon)));
    }

    @PostMapping("/transactions/suppliers/{maHoaDon}/confirm")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<Void>> confirmSupplierPayment(
            @PathVariable UUID maHoaDon,
            @RequestParam String phuongThuc) {
        financeService.confirmSupplierPayment(maHoaDon, phuongThuc);
        return ResponseEntity.ok(ApiResponse.success(null, "Xác nhận chi tiền cho NCC thành công"));
    }

    @PostMapping("/transactions/{maHoaDon}/cancel")
    @PreAuthorize("hasAnyAuthority('Administrator', 'FINANCE')")
    public ResponseEntity<ApiResponse<Void>> cancelTransaction(@PathVariable UUID maHoaDon) {
        financeService.cancelTransaction(maHoaDon);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã hủy hóa đơn thành công"));
    }
}