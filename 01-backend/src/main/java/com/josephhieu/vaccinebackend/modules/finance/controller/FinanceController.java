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

/**
 * Controller chịu trách nhiệm quản trị dòng tiền, danh mục giá vắc-xin và các giao dịch tài chính.
 * <p>
 * Module này kết nối trực tiếp với Inventory (để thanh toán nhập kho) và Vaccination (để thu phí tiêm chủng),
 * đảm bảo tính toàn vẹn dữ liệu tài chính theo quy trình SRS v3.0.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@Slf4j
public class FinanceController {

    private final FinanceService financeService;

    // =========================================================================
    // PHÂN HỆ 0: TỔNG QUAN (DASHBOARD ANALYTICS)
    // =========================================================================

    /**
     * Truy xuất dữ liệu tổng quan về tình hình tài chính (Doanh thu, Chi phí, Lợi nhuận).
     * Phục vụ cho các thẻ Summary hiển thị tại Header của giao diện quản trị.
     *
     * @return {@link ResponseEntity} chứa các chỉ số tài chính tổng hợp.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<FinanceSummaryResponse>> getSummary() {
        log.info("Yêu cầu truy xuất báo cáo tổng quan tài chính hệ thống.");
        return ResponseEntity.ok(ApiResponse.success(financeService.getFinanceSummary()));
    }

    /**
     * Thống kê chi tiết các khoản chi và công nợ liên quan đến Nhà cung cấp.
     *
     * @return {@link ResponseEntity} tóm tắt tình hình tài chính phía đối tác.
     */
    @GetMapping("/summary/suppliers")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<SupplierSummaryResponse>> getSupplierSummary() {
        log.info("Yêu cầu truy xuất tóm tắt giao dịch nhà cung cấp.");
        return ResponseEntity.ok(ApiResponse.success(financeService.getSupplierSummary()));
    }

    // =========================================================================
    // PHÂN HỆ 1: QUẢN LÝ DANH MỤC VẮC XIN & ĐỊNH GIÁ
    // =========================================================================

    /**
     * Danh sách vắc-xin kèm thông tin giá bán và định mức tài chính, hỗ trợ phân trang.
     *
     * @param page Trang hiện tại (1-based).
     * @param size Số lượng bản ghi mỗi trang.
     * @param search Từ khóa tìm kiếm theo tên hoặc mã vắc-xin.
     * @return {@link ResponseEntity} trang dữ liệu danh mục vắc-xin.
     */
    @GetMapping("/vaccines")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<PageResponse<VaccineFullResponse>>> getVaccineList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String search) {
        log.info("Truy vấn danh mục vắc-xin quản lý tài chính - Trang: {}", page);
        return ResponseEntity.ok(ApiResponse.success(financeService.getVaccineManagementList(page, size, search)));
    }

    /**
     * Thiết lập một loại vắc-xin mới vào danh mục kinh doanh của trung tâm.
     *
     * @param request Thông tin kỹ thuật và giá cả của vắc-xin.
     * @return {@link ResponseEntity} với mã 201 (Created) và dữ liệu vắc-xin vừa tạo.
     */
    @PostMapping("/vaccines")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<VaccineFullResponse>> create(@RequestBody @Valid VaccineFullRequest request) {
        log.info("Thêm mới vắc-xin vào danh mục kinh doanh: {}", request.getTenVacXin());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(financeService.createVaccine(request), "Thêm mới vắc-xin thành công"));
    }

    /**
     * Cập nhật thông tin định giá hoặc dữ liệu hành chính của vắc-xin hiện có.
     *
     * @param id Định danh vắc-xin cần cập nhật.
     * @param request Thông tin cập nhật mới.
     * @return {@link ResponseEntity} dữ liệu sau khi chỉnh sửa.
     */
    @PutMapping("/vaccines/{id}")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<VaccineFullResponse>> update(@PathVariable UUID id, @RequestBody @Valid VaccineFullRequest request) {
        log.info("Cập nhật thông tin tài chính cho vắc-xin ID: {}", id);
        return ResponseEntity.ok(ApiResponse.success(financeService.updateVaccine(id, request), "Cập nhật vắc-xin thành công"));
    }

    /**
     * Gỡ bỏ vắc-xin khỏi danh mục kinh doanh hiện tại.
     *
     * @param id Mã vắc-xin cần xóa.
     * @return {@link ResponseEntity} xác nhận thao tác thành công.
     */
    @DeleteMapping("/vaccines/{id}")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.warn("Thực hiện lệnh xóa vắc-xin khỏi danh mục kinh doanh ID: {}", id);
        financeService.deleteVaccine(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa vắc-xin thành công"));
    }

    // =========================================================================
    // PHÂN HỆ 2: GIAO DỊCH KHÁCH HÀNG (QUẢN LÝ DOANH THU)
    // =========================================================================

    /**
     * Truy xuất lịch sử hóa đơn tiêm chủng của khách hàng theo thời gian.
     *
     * @param startDate Ngày bắt đầu (yyyy-MM-dd).
     * @param endDate Ngày kết thúc (yyyy-MM-dd).
     * @return {@link ResponseEntity} danh sách giao dịch khách hàng phân trang.
     */
    @GetMapping("/transactions/customers")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<PageResponse<CustomerTransactionResponse>>> getCustomerTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("Truy xuất giao dịch khách hàng từ {} đến {}", startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(financeService.getCustomerTransactions(page, size, search, startDate, endDate)));
    }

    /**
     * Xác nhận thanh toán hóa đơn tiêm chủng.
     * Chuyển trạng thái hóa đơn từ 'Chờ thanh toán' sang 'Đã thanh toán'.
     *
     * @param maHoaDon ID của hóa đơn khách hàng.
     * @param phuongThuc Hình thức thanh toán (Tiền mặt, Chuyển khoản, v.v.).
     * @return {@link ResponseEntity} thông báo trạng thái.
     */
    @PostMapping("/transactions/customers/{maHoaDon}/confirm")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(@PathVariable UUID maHoaDon, @RequestParam String phuongThuc) {
        log.info("Xác nhận thanh toán cho hóa đơn khách hàng: {}", maHoaDon);
        financeService.confirmPayment(maHoaDon, phuongThuc);
        return ResponseEntity.ok(ApiResponse.success(null, "Xác nhận thanh toán thành công"));
    }

    // =========================================================================
    // PHÂN HỆ 3: GIAO DỊCH NHÀ CUNG CẤP (QUẢN LÝ CHI PHÍ)
    // =========================================================================

    /**
     * Lấy danh sách các hóa đơn nhập hàng từ Nhà cung cấp.
     */
    @GetMapping("/transactions/suppliers")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<PageResponse<SupplierTransactionResponse>>> getSupplierTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        log.info("Truy xuất lịch sử chi tiền cho nhà cung cấp.");
        return ResponseEntity.ok(ApiResponse.success(financeService.getSupplierTransactions(page, size, search)));
    }

    /**
     * Xem chi tiết nội dung một hóa đơn nhập hàng bao gồm danh sách lô vắc-xin đi kèm.
     */
    @GetMapping("/transactions/suppliers/{maHoaDon}")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<HoaDon>> getSupplierDetail(@PathVariable UUID maHoaDon) {
        log.info("Truy vấn chi tiết hóa đơn nhập kho: {}", maHoaDon);
        return ResponseEntity.ok(ApiResponse.success(financeService.getSupplierTransactionDetail(maHoaDon)));
    }

    /**
     * Xác nhận đã thực hiện chi tiền trả cho Nhà cung cấp đối với các lô hàng đã nhập kho.
     */
    @PostMapping("/transactions/suppliers/{maHoaDon}/confirm")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<Void>> confirmSupplierPayment(
            @PathVariable UUID maHoaDon,
            @RequestParam String phuongThuc) {
        log.info("Xác nhận tất toán hóa đơn cho nhà cung cấp: {}", maHoaDon);
        financeService.confirmSupplierPayment(maHoaDon, phuongThuc);
        return ResponseEntity.ok(ApiResponse.success(null, "Xác nhận chi tiền cho NCC thành công"));
    }

    /**
     * Thực hiện hủy hóa đơn trong trường hợp sai sót hoặc giao dịch không thành công.
     */
    @PostMapping("/transactions/{maHoaDon}/cancel")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Tài chính')")
    public ResponseEntity<ApiResponse<Void>> cancelTransaction(@PathVariable UUID maHoaDon) {
        log.warn("Yêu cầu hủy hóa đơn hệ thống: {}", maHoaDon);
        financeService.cancelTransaction(maHoaDon);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã hủy hóa đơn thành công"));
    }
}