package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.FeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.FeedbackResponse;
import com.josephhieu.vaccinebackend.modules.medical.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medical/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * API Gửi phản hồi sau khi tiêm.
     * Chỉ dành cho tài khoản Bệnh nhân (Normal User Account).
     */
    @PostMapping
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<Void> sendFeedback(@RequestBody @Valid FeedbackRequest request) {
        feedbackService.sendFeedback(request);
        return ApiResponse.<Void>builder()
                .message("Cảm ơn bạn đã gửi phản hồi. Chúng tôi đã ghi nhận thông tin!")
                .build();
    }

    /**
     * API Lấy danh sách lịch sử phản hồi của chính bệnh nhân đang đăng nhập.
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<List<FeedbackResponse>> getMyFeedbacks() {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getMyFeedbacks())
                .build();
    }
}