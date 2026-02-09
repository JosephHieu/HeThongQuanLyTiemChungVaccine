package com.josephhieu.vaccinebackend.modules.finance.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.VaccineFullResponse;
import com.josephhieu.vaccinebackend.modules.finance.service.FinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance/vaccines")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    /**
     * Lấy danh sách quản lý vắc-xin đầy đủ thông tin (Phân trang)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<PageResponse<VaccineFullResponse>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "7") int size, // Mặc định là 7 như bạn muốn
            @RequestParam(required = false) String search // Thêm tham số này
    ) {
        return ApiResponse.<PageResponse<VaccineFullResponse>>builder()
                .result(financeService.getVaccineManagementList(page, size, search))
                .build();
    }

    /**
     * API thêm mới vắc-xin vào danh mục
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<VaccineFullResponse> create(@RequestBody @Valid VaccineFullRequest request) {
        return ApiResponse.<VaccineFullResponse>builder()
                .message("Thêm mới vắc-xin thành công")
                .result(financeService.createVaccine(request))
                .build();
    }

    /**
     * API cập nhật toàn diện thông tin vắc-xin
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<VaccineFullResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid VaccineFullRequest request
    ) {
        return ApiResponse.<VaccineFullResponse>builder()
                .message("Cập nhật thông tin vắc-xin thành công")
                .result(financeService.updateVaccine(id, request))
                .build();
    }

    /**
     * API xóa vắc-xin (Có ràng buộc kiểm tra lô hàng)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        financeService.deleteVaccine(id);
        return ApiResponse.<Void>builder()
                .message("Xóa vắc-xin khỏi danh mục thành công")
                .build();
    }

    /**
     * API lấy tổng giá trị hàng tồn kho (Dựa trên giá nhập)
     * Đây là endpoint mà Frontend đang báo lỗi 500 vì thiếu.
     */
    @GetMapping("/total-inventory-value")
    @PreAuthorize("hasAnyRole('Administrator', 'Tài chính')")
    public ApiResponse<BigDecimal> getTotalValue() {
        return ApiResponse.<BigDecimal>builder()
                .message("Truy xuất tổng giá trị kho thành công")
                .result(financeService.calculateTotalInventoryValue())
                .build();
    }
}