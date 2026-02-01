package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.modules.medical.dto.request.PrescribeRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdatePatientRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.MedicalRecordResponse;

import java.util.UUID;

/**
 * Interface định nghĩa các nghiệp vụ liên quan đến Hồ sơ bệnh án.
 * Bao gồm: Truy xuất thông tin tổng hợp, Cập nhật hành chính và Kê đơn tiêm chủng.
 * * @author Joseph Hieu
 */
public interface MedicalRecordService {

    /**
     * Lấy dữ liệu tổng hợp của bệnh nhân (Thông tin cá nhân, Mũi tiêm gần nhất, Lịch hẹn tiếp theo).
     * @param maBenhNhan ID duy nhất của bệnh nhân.
     * @return MedicalRecordResponse chứa dữ liệu phẳng hóa cho Frontend.
     */
    MedicalRecordResponse getMedicalRecord(UUID maBenhNhan);

    /**
     * Cập nhật thông tin hành chính của bệnh nhân.
     * @param maBenhNhan ID bệnh nhân cần cập nhật.
     * @param request DTO chứa các thông tin thay đổi.
     * @return MedicalRecordResponse Thông tin sau khi đã cập nhật.
     */
    MedicalRecordResponse updatePatientInfo(UUID maBenhNhan, UpdatePatientRequest request);

    /**
     * Kê đơn (chỉ định) một mũi tiêm chủng mới cho bệnh nhân.
     * @param maBenhNhan ID bệnh nhân được chỉ định.
     * @param request DTO chứa mã lô vắc-xin và ngày hẹn.
     */
    void prescribeVaccine(UUID maBenhNhan, PrescribeRequest request);
}
