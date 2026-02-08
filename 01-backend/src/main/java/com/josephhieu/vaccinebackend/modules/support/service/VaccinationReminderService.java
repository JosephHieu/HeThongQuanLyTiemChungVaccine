package com.josephhieu.vaccinebackend.modules.support.service;

import com.josephhieu.vaccinebackend.modules.support.dto.request.VaccinationReminderRequest;
import com.josephhieu.vaccinebackend.modules.support.dto.response.VaccinationReminderResponse;

public interface VaccinationReminderService {

    // 1. Tra cứu thông tin tổng hợp để hiển thị lên bảng
    VaccinationReminderResponse getPatientDataByEmail(String email);

    // 2. Xử lý logic gửi email
    void sendReminderEmail(VaccinationReminderRequest request);
}
