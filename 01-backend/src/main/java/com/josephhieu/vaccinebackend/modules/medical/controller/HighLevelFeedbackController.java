package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.HighLevelFeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.HighLevelFeedbackResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.service.HighLevelFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller cung cấp các điểm cuối (endpoints) cho chức năng Phản hồi cấp cao.
 * Đảm bảo phân quyền nghiêm ngặt giữa Bệnh nhân và Quản trị viên hệ thống.
 * * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/medical/high-level-feedback")
@RequiredArgsConstructor
@Slf4j
public class HighLevelFeedbackController {

    private final HighLevelFeedbackService highLevelFeedbackService;

    // ========================================================================
    // DÀNH CHO BỆNH NHÂN (NORMAL USER)
    // ========================================================================

    /**
     * Gửi một phản hồi mới tới ban quản trị.
     * Chỉ dành cho người dùng có vai trò 'Normal User Account'.
     */
    @PostMapping
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<Void> sendFeedback(@RequestBody @Valid HighLevelFeedbackRequest request) {
        log.info("API: Tiếp nhận yêu cầu gửi phản hồi mới từ người dùng.");
        highLevelFeedbackService.sendFeedback(request);
        return ApiResponse.<Void>builder()
                .message("Phản hồi của bạn đã được gửi tới Administrator thành công!")
                .build();
    }

    /**
     * Tra cứu lịch sử các phản hồi cá nhân đã gửi.
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<List<HighLevelFeedbackResponse>> getMyHistory() {
        log.info("API: Truy xuất lịch sử phản hồi cá nhân.");
        return ApiResponse.<List<HighLevelFeedbackResponse>>builder()
                .result(highLevelFeedbackService.getMyFeedbackHistory())
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<Void> updateFeedback(@PathVariable UUID id, @RequestBody @Valid HighLevelFeedbackRequest request) {
        highLevelFeedbackService.updateFeedback(id, request);
        return ApiResponse.<Void>builder().message("Cập nhật phản hồi thành công!").build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<Void> deleteMyFeedback(@PathVariable UUID id) {
        highLevelFeedbackService.deleteMyFeedback(id);
        return ApiResponse.<Void>builder().message("Đã xóa phản hồi.").build();
    }

    // ========================================================================
    // DÀNH CHO QUẢN TRỊ VIÊN (ADMINISTRATOR)
    // ========================================================================

    /**
     * Lấy danh sách toàn bộ phản hồi trong hệ thống để xử lý.
     * Chỉ dành cho Administrator.
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('Administrator')")
    public ApiResponse<List<HighLevelFeedbackResponse>> getAllForAdmin() {
        log.info("API: Administrator đang truy cập danh sách phản hồi tổng hợp.");
        return ApiResponse.<List<HighLevelFeedbackResponse>>builder()
                .result(highLevelFeedbackService.getAllFeedbackForAdmin())
                .build();
    }

    /**
     * Cập nhật trạng thái xử lý cho phản hồi (Ví dụ: Tiếp nhận, Đã giải quyết).
     * * @param id Mã định danh phản hồi.
     * @param status Trạng thái mới (0, 1, 2).
     */
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('Administrator')")
    public ApiResponse<Void> updateStatus(
            @PathVariable UUID id,
            @RequestParam Integer status) {
        log.info("API: Cập nhật trạng thái phản hồi ID: {} sang mức: {}", id, status);
        highLevelFeedbackService.updateStatus(id, status);
        return ApiResponse.<Void>builder()
                .message("Đã cập nhật trạng thái phản hồi thành công.")
                .build();
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('Administrator')")
    public ApiResponse<Void> deleteByAdmin(@PathVariable UUID id) {
        highLevelFeedbackService.deleteFeedbackByAdmin(id);
        return ApiResponse.<Void>builder().message("Admin đã xóa bản ghi phản hồi.").build();
    }

    @GetMapping("/types")
    public ApiResponse<List<LoaiPhanHoi>> getFeedbackTypes() {
        return ApiResponse.<List<LoaiPhanHoi>>builder()
                .result(highLevelFeedbackService.getFeedbackTypes())
                .message("Lấy danh sách Loại phản hồi thành công")
                .build();
    }
}