package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.EpidemicRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.EpidemicResponse;
import com.josephhieu.vaccinebackend.modules.medical.service.EpidemicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller quản lý dữ liệu dịch tễ và tình hình dịch bệnh địa phương.
 * <p>
 * Cung cấp khả năng công khai thông tin dịch bệnh cho người dân (Normal User)
 * và các công cụ quản lý, cập nhật dữ liệu khảo sát cho nhân viên y tế (Medical Staff).
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */

@RestController
@RequestMapping("/api/v1/medical/epidemics")
@RequiredArgsConstructor
@Slf4j
public class EpidemicController {

    private final EpidemicService epidemicService;

    /**
     * Truy xuất toàn bộ danh sách tình hình dịch bệnh đang được ghi nhận trên hệ thống.
     * <p>
     * Dữ liệu này hỗ trợ cả người dùng cá nhân trong việc theo dõi nguy cơ dịch bệnh
     * và nhân viên quản lý trong việc tổng hợp báo cáo.
     * </p>
     *
     * @return {@link ResponseEntity} chứa danh sách toàn bộ các ổ dịch và tình trạng tương ứng.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('Normal User Account', 'Administrator', 'Nhân viên y tế')")
    public ResponseEntity<ApiResponse<List<EpidemicResponse>>> getAllEpidemics() {
        log.info("Yêu cầu truy xuất toàn bộ dữ liệu tình hình dịch bệnh.");
        List<EpidemicResponse> result = epidemicService.getAllEpidemics();

        return ResponseEntity.ok(ApiResponse.success(result, "Tải danh sách tình hình dịch bệnh thành công."));
    }

    /**
     * Tìm kiếm và lọc thông tin dịch bệnh dựa trên vị trí địa lý hoặc địa chỉ cụ thể.
     *
     * @param diaChi Từ khóa địa điểm cần tra cứu dịch tễ.
     * @return {@link ResponseEntity} danh sách các vụ dịch trùng khớp với địa điểm tìm kiếm.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('Normal User Account', 'Administrator', 'Nhân viên y tế')")
    public ResponseEntity<ApiResponse<List<EpidemicResponse>>> searchByLocation(@RequestParam String diaChi) {
        log.info("Thực hiện tra cứu dịch tễ tại khu vực: {}", diaChi);
        List<EpidemicResponse> result = epidemicService.searchByLocation(diaChi);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Ghi nhận một thông tin khảo sát dịch tễ mới vào hệ thống.
     * <p>
     * Chức năng này dành riêng cho nhân viên y tế nhằm cập nhật diễn biến dịch bệnh
     * mới phát sinh để đưa ra các khuyến cáo tiêm chủng phù hợp.
     * </p>
     *
     * @param request Thông tin chi tiết về vụ dịch hoặc khảo sát dịch tễ.
     * @return {@link ResponseEntity} với mã 201 (Created) xác nhận bản ghi đã được tạo.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('Nhân viên y tế', 'Administrator')")
    public ResponseEntity<ApiResponse<Void>> createEpidemic(@RequestBody @Valid EpidemicRequest request) {
        log.info("Nhân viên y tế tạo mới hồ sơ khảo sát dịch tễ tại: {}", request.getDiaChi());
        epidemicService.createEpidemic(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Cập nhật dữ liệu khảo sát dịch tễ thành công!"));
    }

    /**
     * Xem thông tin chi tiết của một vụ dịch hoặc khảo sát dịch tễ dựa trên mã định danh.
     *
     * @param id Mã định danh duy nhất (UUID) của bản ghi dịch bệnh.
     * @return {@link ResponseEntity} chứa thông tin chi tiết của bản ghi.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Normal User Account', 'Administrator', 'Nhân viên y tế')")
    public ResponseEntity<ApiResponse<EpidemicResponse>> getById(@PathVariable UUID id) {
        log.info("Truy xuất chi tiết bản ghi dịch bệnh ID: {}", id);
        EpidemicResponse result = epidemicService.getEpidemicById(id);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Chỉnh sửa và cập nhật thông tin diễn biến của một vụ dịch bệnh hiện có.
     *
     * @param id Mã định danh của bản ghi cần cập nhật.
     * @param request Thông tin chỉnh sửa mới.
     * @return {@link ResponseEntity} chứa dữ liệu sau khi đã được cập nhật thành công.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Nhân viên y tế', 'Administrator')")
    public ResponseEntity<ApiResponse<EpidemicResponse>> update(@PathVariable UUID id, @RequestBody @Valid EpidemicRequest request) {
        log.info("Cập nhật thông tin bản ghi dịch bệnh ID: {}", id);
        EpidemicResponse result = epidemicService.updateEpidemic(id, request);

        return ResponseEntity.ok(ApiResponse.success(result, "Cập nhật thông tin dịch bệnh thành công!"));
    }

    /**
     * Loại bỏ hoàn toàn một bản ghi dịch bệnh khỏi hệ thống dữ liệu.
     *
     * @param id Mã định danh của bản ghi cần xóa.
     * @return {@link ResponseEntity} xác nhận thao tác xóa đã hoàn tất.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Nhân viên y tế', 'Administrator')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.warn("Thực hiện lệnh xóa bản ghi dịch bệnh ID: {}", id);
        epidemicService.deleteEpidemic(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa bản ghi dịch bệnh thành công!"));
    }

}
