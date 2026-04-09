package com.josephhieu.vaccinebackend.modules.finance.service;

import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.*;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service quản lý danh mục vắc-xin và các nghiệp vụ tài chính liên quan.
 * Cung cấp khả năng quản lý toàn diện (CRUD) và thống kê giá trị tài sản kho.
 */
public interface FinanceService {

    // =========================================================================
    // PHÂN HỆ 1: QUẢN LÝ VẮC-XIN (GIỮ NGUYÊN HOẶC TỐI ƯU)
    // =========================================================================

    PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size, String keyword);

    VaccineFullResponse createVaccine(VaccineFullRequest request);

    VaccineFullResponse updateVaccine(UUID id, VaccineFullRequest request);

    void deleteVaccine(UUID id);

    VaccineFullResponse getVaccineDetail(UUID id);

    BigDecimal calculateTotalInventoryValue();


    // =========================================================================
    // PHÂN HỆ 2: GIAO DỊCH KHÁCH HÀNG (THU TIỀN)
    // =========================================================================

    PageResponse<CustomerTransactionResponse> getCustomerTransactions(
            int page, int size, String search, String startDate, String endDate);

    void confirmPayment(UUID maHoaDon, String phuongThucThanhToan);

    void cancelTransaction(UUID maHoaDon);


    // =========================================================================
    // PHÂN HỆ 3: GIAO DỊCH NHÀ CUNG CẤP (CHI TIỀN) - CẬP NHẬT MỚI
    // =========================================================================

    /**
     * SỬA: Trả về SupplierTransactionResponse thay vì HoaDon để có tên Nhà cung cấp.
     */
    PageResponse<SupplierTransactionResponse> getSupplierTransactions(
            int page, int size, String search);

    /**
     * MỚI: Lấy chi tiết đơn nhập hàng (bao gồm danh sách lô vắc-xin bên trong).
     */
    HoaDon getSupplierTransactionDetail(UUID maHoaDon);

    /**
     * MỚI: Xác nhận đã trả tiền cho Nhà cung cấp.
     */
    void confirmSupplierPayment(UUID maHoaDon, String phuongThuc);


    // =========================================================================
    // PHÂN HỆ 4: TỔNG QUAN & THỐNG KÊ - CẬP NHẬT MỚI
    // =========================================================================

    FinanceSummaryResponse getFinanceSummary();

    /**
     * MỚI: Lấy dữ liệu thống kê riêng cho Tab Nhà cung cấp.
     */
    SupplierSummaryResponse getSupplierSummary();
}