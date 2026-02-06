package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.EpidemicRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.EpidemicResponse;
import com.josephhieu.vaccinebackend.modules.medical.service.EpidemicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medical/epidemics")
@RequiredArgsConstructor
public class EpidemicController {

    private final EpidemicService epidemicService;

    /**
     * Lấy danh sách toàn bộ tình hình dịch bệnh (Dành cho cả User và Staff).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Normal User Account', 'Administrator', 'Nhân viên y tế')")
    public ApiResponse<List<EpidemicResponse>> getAllEpidemics() {
        return ApiResponse.<List<EpidemicResponse>>builder()
                .result(epidemicService.getAllEpidemics())
                .build();
    }

    /**
     * Tra cứu dịch bệnh theo vị trí/địa chỉ.
     * @param diaChi Từ khóa tìm kiếm (Query Parameter: ?diaChi=...)
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('Normal User Account', 'Administrator', 'Nhân viên y tế')")
    public ApiResponse<List<EpidemicResponse>> searchByLocation(@RequestParam String diaChi) {
        return ApiResponse.<List<EpidemicResponse>>builder()
                .result(epidemicService.searchByLocation(diaChi))
                .build();
    }

    /**
     * Tạo mới thông tin khảo sát dịch bệnh.
     * Chỉ cho phép 'Nhân viên y tế' hoặc 'Administrator' thực hiện.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Nhân viên y tế', 'Administrator')")
    public ApiResponse<Void> createEpidemic(@RequestBody @Valid EpidemicRequest request) {
        epidemicService.createEpidemic(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật dữ liệu khảo sát dịch tễ thành công!")
                .build();
    }

    /**
     * Lấy chi tiết thông tin một vụ dịch bệnh cụ thể.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Normal User Account', 'Administrator', 'Nhân viên y tế')")
    public ApiResponse<EpidemicResponse> getById(@PathVariable java.util.UUID id) {
        return ApiResponse.<EpidemicResponse>builder()
                .result(epidemicService.getEpidemicById(id))
                .build();
    }

    /**
     * Cập nhật thông tin dịch bệnh.
     * Chỉ cho phép 'Nhân viên y tế' hoặc 'Administrator' thực hiện.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Nhân viên y tế', 'Administrator')")
    public ApiResponse<EpidemicResponse> update(@PathVariable java.util.UUID id, @RequestBody @Valid EpidemicRequest request) {
        return ApiResponse.<EpidemicResponse>builder()
                .result(epidemicService.updateEpidemic(id, request))
                .message("Cập nhật thông tin dịch bệnh thành công!")
                .build();
    }

    /**
     * Xóa bản ghi dịch bệnh khỏi hệ thống.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Nhân viên y tế', 'Administrator')")
    public ApiResponse<Void> delete(@PathVariable java.util.UUID id) {
        epidemicService.deleteEpidemic(id);
        return ApiResponse.<Void>builder()
                .message("Đã xóa bản ghi dịch bệnh thành công!")
                .build();
    }


}
