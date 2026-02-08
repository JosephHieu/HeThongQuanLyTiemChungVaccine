package com.josephhieu.vaccinebackend.modules.support.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.support.dto.request.VaccinationReminderRequest;
import com.josephhieu.vaccinebackend.modules.support.dto.response.VaccinationReminderResponse;
import com.josephhieu.vaccinebackend.modules.support.service.VaccinationReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/support/reminders")
@RequiredArgsConstructor
public class VaccinationReminderController {

    private final VaccinationReminderService reminderService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('Administrator', 'Hỗ trợ khách hàng')")
    public ApiResponse<VaccinationReminderResponse> searchByEmail(@RequestParam String email) {
        return ApiResponse.<VaccinationReminderResponse>builder()
                .result(reminderService.getPatientDataByEmail(email))
                .build();
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('Administrator', 'Hỗ trợ khách hàng')")
    public ApiResponse<Void> sendEmail(@RequestBody VaccinationReminderRequest request) {
        reminderService.sendReminderEmail(request);
        return ApiResponse.<Void>builder()
                .message("Email nhắc lịch đã được gửi thành công!")
                .build();
    }

}
