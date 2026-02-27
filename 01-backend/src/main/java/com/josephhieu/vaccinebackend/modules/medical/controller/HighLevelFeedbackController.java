package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.HighLevelFeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.HighLevelFeedbackResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.service.HighLevelFeedbackService;
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
 * Controller quản trị các phản hồi cấp cao và kênh tương tác trực tiếp với Ban quản trị.
 * <p>
 * Đảm bảo quy trình xử lý khiếu nại, góp ý chuyên sâu được thực hiện minh bạch
 * thông qua cơ chế phân quyền nghiêm ngặt giữa Bệnh nhân và Administrator.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/medical/high-level-feedback")
@RequiredArgsConstructor
@Slf4j
public class HighLevelFeedbackController {

    private final HighLevelFeedbackService highLevelFeedbackService;

    // ========================================================================
    // PHÂN HỆ DÀNH CHO BỆNH NHÂN (USER OPERATIONS)
    // ========================================================================

    /**
     * Gửi yêu cầu phản hồi hoặc khiếu nại mới tới Ban quản trị hệ thống.
     *
     * @param request Nội dung chi tiết của phản hồi.
     * @return {@link ResponseEntity} với mã 201 (Created) xác nhận yêu cầu đã được tiếp nhận.
     */
    @PostMapping
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<Void>> sendFeedback(@RequestBody @Valid HighLevelFeedbackRequest request) {
        log.info("Khách hàng đang khởi tạo một phản hồi cấp cao mới.");
        highLevelFeedbackService.sendFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Phản hồi của bạn đã được gửi tới Administrator thành công!"));
    }

    /**
     * Tra cứu lịch sử toàn bộ các khiếu nại/phản hồi mà cá nhân người dùng đã gửi.
     *
     * @return {@link ResponseEntity} chứa danh sách phản hồi cá nhân.
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<List<HighLevelFeedbackResponse>>> getMyHistory() {
        log.info("Người dùng thực hiện tra cứu lịch sử phản hồi cá nhân.");
        List<HighLevelFeedbackResponse> result = highLevelFeedbackService.getMyFeedbackHistory();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Cập nhật nội dung phản hồi đã gửi (khi yêu cầu đang trong trạng thái chờ xử lý).
     *
     * @param id Mã định danh phản hồi cần chỉnh sửa.
     * @param request Nội dung cập nhật mới.
     * @return {@link ResponseEntity} xác nhận thao tác thành công.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<Void>> updateFeedback(@PathVariable UUID id, @RequestBody @Valid HighLevelFeedbackRequest request) {
        log.info("Người dùng cập nhật nội dung phản hồi ID: {}", id);
        highLevelFeedbackService.updateFeedback(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật phản hồi thành công!"));
    }

    /**
     * Gỡ bỏ hoàn toàn phản hồi cá nhân khỏi hệ thống.
     *
     * @param id Mã định danh của phản hồi.
     * @return {@link ResponseEntity} xác nhận đã xóa dữ liệu.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<Void>> deleteMyFeedback(@PathVariable UUID id) {
        log.warn("Người dùng yêu cầu xóa phản hồi cá nhân ID: {}", id);
        highLevelFeedbackService.deleteMyFeedback(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa phản hồi."));
    }

    // ========================================================================
    // PHÂN HỆ DÀNH CHO QUẢN TRỊ VIÊN (ADMIN OPERATIONS)
    // ========================================================================

    /**
     * Truy xuất toàn bộ phản hồi từ tất cả người dùng trong hệ thống để quản lý tập trung.
     *
     * @return {@link ResponseEntity} danh sách tổng hợp dành cho Administrator.
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<ApiResponse<List<HighLevelFeedbackResponse>>> getAllForAdmin() {
        log.info("Quản trị viên đang truy cập danh sách phản hồi tổng hợp toàn hệ thống.");
        List<HighLevelFeedbackResponse> result = highLevelFeedbackService.getAllFeedbackForAdmin();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Cập nhật trạng thái xử lý cho phản hồi (Ví dụ: Chờ xử lý, Đang giải quyết, Hoàn tất).
     *
     * @param id Mã định danh của phản hồi cần điều chỉnh.
     * @param status Mã trạng thái mới (0, 1, 2).
     * @return {@link ResponseEntity} thông báo cập nhật thành công.
     */
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable UUID id,
            @RequestParam Integer status) {
        log.info("Administrator thay đổi trạng thái phản hồi ID: {} sang mức: {}", id, status);
        highLevelFeedbackService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã cập nhật trạng thái phản hồi thành công."));
    }

    /**
     * Administrator thực hiện xóa vĩnh viễn một bản ghi phản hồi khỏi cơ sở dữ liệu.
     *
     * @param id Mã phản hồi cần xóa.
     * @return {@link ResponseEntity} xác nhận xóa bởi quyền quản trị.
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('Administrator')")
    public ResponseEntity<ApiResponse<Void>> deleteByAdmin(@PathVariable UUID id) {
        log.warn("Lệnh xóa bản ghi bởi Administrator đối với phản hồi ID: {}", id);
        highLevelFeedbackService.deleteFeedbackByAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Admin đã xóa bản ghi phản hồi thành công."));
    }

    /**
     * Lấy danh mục các loại phản hồi được hỗ trợ trên hệ thống.
     * Phục vụ cho việc đổ dữ liệu vào các ô chọn (Dropdown) trên giao diện.
     *
     * @return {@link ResponseEntity} danh sách các đối tượng {@link LoaiPhanHoi}.
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<LoaiPhanHoi>>> getFeedbackTypes() {
        log.info("Truy xuất danh mục phân loại phản hồi.");
        List<LoaiPhanHoi> result = highLevelFeedbackService.getFeedbackTypes();
        return ResponseEntity.ok(ApiResponse.success(result, "Lấy danh sách Loại phản hồi thành công."));
    }
}