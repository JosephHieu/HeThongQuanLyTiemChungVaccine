package com.josephhieu.vaccinebackend.modules.finance.service;

import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.VaccineFullResponse;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service quản lý danh mục vắc-xin và các nghiệp vụ tài chính liên quan.
 * Cung cấp khả năng quản lý toàn diện (CRUD) và thống kê giá trị tài sản kho.
 */
public interface FinanceService {

    /**
     * Truy xuất danh sách vắc-xin đầy đủ phục vụ quản lý danh mục và giá.
     */
    PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size);

    /**
     * Tạo mới một loại vắc-xin vào hệ thống.
     */
    VaccineFullResponse createVaccine(VaccineFullRequest request);

    /**
     * Cập nhật toàn diện thông tin vắc-xin dựa trên ID.
     */
    VaccineFullResponse updateVaccine(UUID id, VaccineFullRequest request);

    /**
     * Xóa vắc-xin khỏi danh mục (Chỉ cho phép nếu chưa có dữ liệu liên quan).
     */
    void deleteVaccine(UUID id);

    /**
     * Tính toán tổng giá trị tồn kho hiện tại (Dựa trên giá nhập và số lượng).
     */
    BigDecimal calculateTotalInventoryValue();

    /**
     * Lấy chi tiết thông tin vắc-xin theo ID.
     */
    VaccineFullResponse getVaccineDetail(UUID id);

    PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size, String keyword);
}