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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller quản trị vòng đời hàng tồn kho vắc-xin.
 * <p>
 * Chịu trách nhiệm điều phối các hoạt động nhập kho (Import), xuất kho điều phối (Export),
 * tra cứu tồn kho theo lô (Batch-tracking) và quản lý danh mục nhà cung cấp.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Truy xuất danh sách tồn kho vắc-xin hiện tại với khả năng tìm kiếm nâng cao và phân trang.
     * <p>
     * Hỗ trợ lọc theo các tiêu chí (criteria) như tên vắc-xin, số lô, hoặc nhà cung cấp.
     * </p>
     *
     * @param criteria Tiêu chí tìm kiếm (mặc định: name).
     * @param search Giá trị tìm kiếm tương ứng.
     * @param page Chỉ số trang hiện tại (mặc định: 0).
     * @param size Số lượng bản ghi trên mỗi trang (mặc định: 10).
     * @return {@link ResponseEntity} chứa trang dữ liệu tồn kho.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<Page<InventoryResponse>>> getInventory(
            @RequestParam(required = false, defaultValue = "name") String criteria,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Yêu cầu tải danh sách kho - Tiêu chí: {}, Từ khóa: {}", criteria, search);
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryResponse> result = inventoryService.getInventoryPage(criteria, search, pageable);

        return ResponseEntity.ok(ApiResponse.success(result, "Tải danh sách kho thành công"));
    }

    /**
     * Thực hiện nhập lô vắc-xin mới vào hệ thống kho.
     * <p>
     * Quy trình bao gồm khởi tạo số lô, cập nhật số lượng tồn và ghi nhận thông tin tài chính liên quan.
     * Sử dụng mã 201 (Created) để xác nhận tài nguyên kho mới đã được thiết lập.
     * </p>
     *
     * @param request Thông tin chi tiết lô hàng nhập kho.
     * @return {@link ResponseEntity} với mã trạng thái 201 và dữ liệu lô vừa nhập.
     */
    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<InventoryResponse>> importVaccine(@RequestBody @Valid VaccineImportRequest request) {
        log.info("Bắt đầu quy trình nhập kho vắc-xin: {}", request.getTenVacXin());
        InventoryResponse result = inventoryService.importVaccine(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Nhập kho vắc-xin mới thành công"));
    }

    /**
     * Xử lý yêu cầu xuất vắc-xin để điều phối tiêm chủng hoặc chuyển kho.
     * <p>
     * Hệ thống sẽ tự động trừ số lượng tồn theo nguyên tắc FEFO (Hết hạn trước - Xuất trước)
     * và sinh phiếu xuất kho tương ứng.
     * </p>
     *
     * @param request Thông tin yêu cầu xuất kho.
     * @return {@link ResponseEntity} xác nhận xuất kho kèm số phiếu xuất.
     */
    @PostMapping("/export")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<VaccineExportResponse>> exportVaccine(@RequestBody @Valid VaccineExportRequest request) {
        log.warn("Thực hiện lệnh xuất kho vắc-xin");
        VaccineExportResponse result = inventoryService.exportVaccine(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Xuất kho thành công! Số phiếu: " + result.getSoPhieuXuat()));
    }

    /**
     * Truy xuất thông tin chi tiết của một lô vắc-xin cụ thể thông qua mã định danh.
     *
     * @param maLo UUID của lô hàng cần tra cứu.
     * @return {@link ResponseEntity} chứa chi tiết lô hàng và trạng thái hiện tại.
     */
    @GetMapping("/{maLo}")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<InventoryResponse>> getBatchDetail(@PathVariable UUID maLo) {
        log.info("Truy vấn chi tiết lô vắc-xin ID: {}", maLo);
        InventoryResponse result = inventoryService.getBatchDetail(maLo);

        return ResponseEntity.ok(ApiResponse.success(result, "Lấy thông tin lô hàng thành công"));
    }

    /**
     * Lấy danh mục toàn bộ nhà cung cấp hiện có trên hệ thống.
     * Thường dùng để cung cấp dữ liệu cho các bộ chọn (Dropdown/Select) trên giao diện nhập kho.
     *
     * @return {@link ResponseEntity} danh sách các đối tượng {@link NhaCungCap}.
     */
    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<List<NhaCungCap>>> getAllSuppliers() {
        log.info("Tải danh mục nhà cung cấp");
        List<NhaCungCap> result = inventoryService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success(result, "Tải danh sách nhà cung cấp thành công"));
    }

    /**
     * Lấy danh mục các loại vắc-xin đang được hệ thống quản lý.
     *
     * @return {@link ResponseEntity} danh sách các đối tượng {@link LoaiVacXin}.
     */
    @GetMapping("/vaccine-types")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<List<LoaiVacXin>>> getAllVaccineTypes() {
        log.info("Tải danh mục loại vắc-xin");
        List<LoaiVacXin> result = inventoryService.getAllVaccineTypes();
        return ResponseEntity.ok(ApiResponse.success(result, "Tải danh sách loại vắc-xin thành công"));
    }

    /**
     * Thống kê tổng số liều vắc-xin hiện có trong toàn bộ hệ thống kho.
     *
     * @return {@link ResponseEntity} tổng số liều (Long).
     */
    @GetMapping("/stats/total-doses")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<Long>> getTotalDoses() {
        log.info("Tính toán tổng số liều tồn kho hiện tại");
        Long result = inventoryService.getTotalDoses();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Truy xuất lịch sử các giao dịch xuất kho trong một khoảng thời gian nhất định.
     * <p>
     * Dữ liệu được sắp xếp theo thời gian xuất kho mới nhất lên đầu để dễ dàng đối soát.
     * </p>
     *
     * @param startDate Thời điểm bắt đầu lọc (Optional).
     * @param endDate Thời điểm kết thúc lọc (Optional).
     * @param page Chỉ số trang.
     * @param size Số bản ghi mỗi trang.
     * @return {@link ResponseEntity} trang dữ liệu lịch sử xuất kho.
     */
    @GetMapping("/export-history")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Quản lý kho')")
    public ResponseEntity<ApiResponse<Page<VaccineExportResponse>>> getExportHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Truy xuất lịch sử xuất kho từ {} đến {}", startDate, endDate);
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayXuat").descending());
        Page<VaccineExportResponse> result = inventoryService.getExportHistory(startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}