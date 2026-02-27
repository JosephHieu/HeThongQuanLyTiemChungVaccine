package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.FeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.FeedbackResponse;
import com.josephhieu.vaccinebackend.modules.medical.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý luồng thông tin phản hồi và đánh giá từ phía bệnh nhân.
 * <p>
 * Cung cấp các giao diện lập trình (API) để bệnh nhân gửi báo cáo về phản ứng sau tiêm
 * hoặc đóng góp ý kiến về chất lượng dịch vụ tại trung tâm tiêm chủng.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/medical/feedback")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Tiếp nhận và lưu trữ phản hồi mới từ bệnh nhân sau khi thực hiện tiêm chủng.
     * <p>
     * Dữ liệu này đóng vai trò quan trọng trong việc theo dõi an toàn tiêm chủng
     * và phát hiện sớm các phản ứng phụ không mong muốn.
     * </p>
     *
     * @param request Đối tượng chứa nội dung phản hồi và đánh giá mức độ hài lòng.
     * @return {@link ResponseEntity} với mã 201 (Created) xác nhận phản hồi đã được ghi nhận.
     */
    @PostMapping
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<Void>> sendFeedback(@RequestBody @Valid FeedbackRequest request) {
        log.info("Tiếp nhận phản hồi mới từ bệnh nhân.");
        feedbackService.sendFeedback(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Cảm ơn bạn đã gửi phản hồi. Chúng tôi đã ghi nhận thông tin!"));
    }

    /**
     * Truy xuất danh sách toàn bộ các phản hồi và ý kiến đóng góp đã gửi của cá nhân bệnh nhân.
     * <p>
     * Giúp người dùng theo dõi lại các báo cáo sức khỏe sau tiêm mà họ đã cung cấp cho hệ thống.
     * </p>
     *
     * @return {@link ResponseEntity} chứa lịch sử phản hồi cá nhân.
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getMyFeedbacks() {
        log.info("Truy xuất lịch sử phản hồi của người dùng hiện tại.");
        List<FeedbackResponse> result = feedbackService.getMyFeedbacks();

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}