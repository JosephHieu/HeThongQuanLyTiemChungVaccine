package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.modules.medical.dto.request.FeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    // Bệnh nhân gửi phản hồi
    void sendFeedback(FeedbackRequest request);

    // Bệnh nhân xem lại lịch sử phản hồi của mình
    List<FeedbackResponse> getMyFeedbacks();

    List<FeedbackResponse> getAllFeedbacks();
}
