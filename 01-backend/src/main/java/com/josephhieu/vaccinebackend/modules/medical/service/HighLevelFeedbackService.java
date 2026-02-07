package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.modules.medical.dto.request.HighLevelFeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.HighLevelFeedbackResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;

import java.util.List;
import java.util.UUID;

/**
 * Service định nghĩa các nghiệp vụ liên quan đến Phản hồi cấp cao (Chức năng 9.5.6).
 * Cho phép bệnh nhân gửi ý kiến trực tiếp tới Administrator và quản lý luồng xử lý phản hồi.
 * * @author Joseph Hieu
 * @version 1.0
 */
public interface HighLevelFeedbackService {

    /**
     * Tiếp nhận và lưu trữ phản hồi mới từ phía bệnh nhân.
     * Hệ thống tự động xác định danh tính bệnh nhân thông qua Security Context.
     *
     * @param request Chứa mã loại phản hồi và nội dung phản hồi.
     */
    void sendFeedback(HighLevelFeedbackRequest request);

    /**
     * Truy xuất danh sách lịch sử các phản hồi mà bệnh nhân hiện tại đã gửi.
     *
     * @return Danh sách các phản hồi đã được định dạng theo DTO Response.
     */
    List<HighLevelFeedbackResponse> getMyFeedbackHistory();

    /**
     * Truy xuất toàn bộ danh sách phản hồi trong hệ thống (Dành riêng cho Administrator).
     *
     * @return Toàn bộ phản hồi sắp xếp theo thời gian mới nhất.
     */
    List<HighLevelFeedbackResponse> getAllFeedbackForAdmin();

    /**
     * Cập nhật trạng thái xử lý của một phản hồi cụ thể.
     *
     * @param id     Mã định danh duy nhất của phản hồi cần cập nhật.
     * @param status Mã trạng thái mới (0: Mới, 1: Đang xử lý, 2: Đã giải quyết).
     */
    void updateStatus(UUID id, Integer status);

    // Dành cho Bệnh nhân
    void updateFeedback(UUID id, HighLevelFeedbackRequest request);
    void deleteMyFeedback(UUID id);

    // Dành cho Admin
    void deleteFeedbackByAdmin(UUID id);

    List<LoaiPhanHoi> getFeedbackTypes();
}
