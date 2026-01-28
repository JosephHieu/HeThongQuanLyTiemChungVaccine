package com.josephhieu.vaccinebackend.modules.inventory.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineExportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineImportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.InventoryResponse;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.VaccineExportResponse;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.NhaCungCap;
import com.josephhieu.vaccinebackend.modules.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller quản lý các giao dịch kho vắc-xin.
 * Cung cấp các Endpoint cho việc tra cứu, nhập và xuất kho.
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * API: Lấy danh sách tồn kho có phân trang và tìm kiếm.
     * Endpoint: GET /api/v1/inventory?criteria=...&search=...&page=0&size=10
     */
    @GetMapping
    public ApiResponse<Page<InventoryResponse>> getInventory(
            @RequestParam(required = false, defaultValue = "name") String criteria,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryResponse> result = inventoryService.getInventoryPage(criteria, search, pageable);

        return ApiResponse.<Page<InventoryResponse>>builder()
                .result(result)
                .message("Tải danh sách kho thành công")
                .build();
    }

    /**
     * API: Nhập lô vắc-xin mới vào kho.
     * Endpoint: POST /api/v1/inventory/import
     */
    @PostMapping("/import")
    public ApiResponse<InventoryResponse> importVaccine(@RequestBody @Valid VaccineImportRequest request) {

        InventoryResponse result = inventoryService.importVaccine(request);

        return ApiResponse.<InventoryResponse>builder()
                .result(result)
                .message("Nhập kho vắc-xin mới thành công")
                .build();
    }

    /**
     * API: Xuất vắc-xin điều phối.
     * Endpoint: POST /api/v1/inventory/export
     */
    @PostMapping("/export")
    public ApiResponse<VaccineExportResponse> exportVaccine(@RequestBody @Valid VaccineExportRequest request) {

        VaccineExportResponse result = inventoryService.exportVaccine(request);

        return ApiResponse.<VaccineExportResponse>builder()
                .result(result)
                .message("Xuất kho thành công! Số phiếu: " + result.getSoPhieuXuat())
                .build();
    }

    /**
     * API: Lấy chi tiết một lô hàng.
     * Endpoint: GET /api/v1/inventory/{maLo}
     */
    @GetMapping("/{maLo}")
    public ApiResponse<InventoryResponse> getBatchDetail(@PathVariable UUID maLo) {

        InventoryResponse result = inventoryService.getBatchDetail(maLo);

        return ApiResponse.<InventoryResponse>builder()
                .result(result)
                .message("Lấy thông tin lô hàng thành công")
                .build();
    }

    /**
     * API: Lấy danh sách tất cả nhà cung cấp (để hiện dropdown).
     */
    @GetMapping("/suppliers")
    public ApiResponse<List<NhaCungCap>> getAllSuppliers() {

        return ApiResponse.<List<NhaCungCap>>builder()
                .result(inventoryService.getAllSuppliers())
                .message("Tải danh sách nhà cung cấp thành công")
                .build();
    }

    /**
     * API: Lấy danh sách các loại vắc-xin.
     */
    @GetMapping("/vaccine-types")
    public ApiResponse<List<LoaiVacXin>> getAllVaccineTypes() {

        return ApiResponse.<List<LoaiVacXin>>builder()
                .result(inventoryService.getAllVaccineTypes())
                .message("Tải danh sách loại vắc-xin thành công")
                .build();
    }

    /**
     * API: Lấy tổng số liều vaccine.
     */
    @GetMapping("/stats/total-doses")
    public ApiResponse<Long> getTotalDoses() {

        return ApiResponse.<Long>builder()
                .result(inventoryService.getTotalDoses())
                .build();
    }
}
