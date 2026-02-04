package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.UpdateProfileRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.PatientProfileResponse;

public interface PatientService {

    /**
     * Lấy thông tin hồ sơ đầy đủ của bệnh nhân đang đăng nhập
     */
    PatientProfileResponse getMyProfile();

    /**
     * Cập nhật thông tin cá nhân của bệnh nhân
     */
    void updateProfile(UpdateProfileRequest request);
}
