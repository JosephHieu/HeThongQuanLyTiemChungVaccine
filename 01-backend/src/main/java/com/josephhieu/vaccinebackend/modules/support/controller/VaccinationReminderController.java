package com.josephhieu.vaccinebackend.modules.support.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.support.dto.request.VaccinationReminderRequest;
import com.josephhieu.vaccinebackend.modules.support.dto.response.VaccinationReminderResponse;
import com.josephhieu.vaccinebackend.modules.support.service.VaccinationReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller chịu trách nhiệm quản lý các dịch vụ hỗ trợ và nhắc lịch tiêm chủng.
 * <p>
 * Phân hệ này cho phép nhân viên hỗ trợ khách hàng tra cứu dữ liệu bệnh nhân dựa trên Email
 * và thực hiện gửi thông báo nhắc lịch tiêm chủng tự động qua giao thức SMTP.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/support/reminders")
@RequiredArgsConstructor
@Slf4j
public class VaccinationReminderController {

    private final VaccinationReminderService reminderService;

    /**
     * Tra cứu thông tin bệnh nhân và các mũi tiêm dự kiến dựa trên địa chỉ Email.
     * <p>
     * Chỉ người dùng có quyền 'Administrator' hoặc 'Hỗ trợ khách hàng' mới có thể thực hiện thao tác này.
     * </p>
     *
     * @param email Địa chỉ email của bệnh nhân cần tra cứu dữ liệu nhắc lịch.
     * @return {@link ResponseEntity} chứa thông tin tổng hợp về lịch tiêm của bệnh nhân.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Hỗ trợ khách hàng')")
    public ResponseEntity<ApiResponse<VaccinationReminderResponse>> searchByEmail(@RequestParam String email) {
        log.info("Nhân viên hỗ trợ thực hiện tra cứu thông tin nhắc lịch cho email: {}", email);
        VaccinationReminderResponse result = reminderService.getPatientDataByEmail(email);

        return ResponseEntity.ok(ApiResponse.success(result, "Tìm thấy thông tin bệnh nhân phù hợp."));
    }

    /**
     * Thực hiện gửi Email nhắc lịch tiêm chủng đến bệnh nhân.
     * <p>
     * Hệ thống sẽ tự động cấu trúc nội dung Email dựa trên thông tin vắc-xin và thời gian
     * trong yêu cầu để gửi đến địa chỉ người nhận.
     * </p>
     *
     * @param request Đối tượng chứa thông tin người nhận và nội dung nhắc lịch tiêm.
     * @return {@link ResponseEntity} xác nhận trạng thái gửi email thành công.
     */
    @PostMapping("/send")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Hỗ trợ khách hàng')")
    public ResponseEntity<ApiResponse<Void>> sendEmail(@RequestBody VaccinationReminderRequest request) {
        log.info("Bắt đầu quy trình gửi email nhắc lịch tiêm chủng đến: {}", request.getEmail());
        reminderService.sendReminderEmail(request);

        return ResponseEntity.ok(ApiResponse.success(null, "Email nhắc lịch đã được gửi thành công!"));
    }

}
