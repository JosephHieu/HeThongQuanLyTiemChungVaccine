package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.PrescribeRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdatePatientRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.MedicalRecordResponse;
import com.josephhieu.vaccinebackend.modules.medical.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Controller cung cấp các API liên quan đến Hồ sơ bệnh án.
 * Tương tác trực tiếp với các Tab: Xem, Cập nhật và Kê đơn ở Frontend.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medical/records")
public class MedicalController {

    private final MedicalRecordService medicalRecordService;

    /**
     * API Truy xuất hồ sơ bệnh án tổng hợp.
     * Dùng cho Tab "Xem hồ sơ".
     */
    @GetMapping("/{id}")
    public ApiResponse<MedicalRecordResponse> getRecord(@PathVariable UUID id) {

        return ApiResponse.<MedicalRecordResponse>builder()
                .result(medicalRecordService.getMedicalRecord(id))
                .build();
    }

    /**
     * API Cập nhật thông tin hành chính bệnh nhân.
     * Dùng cho Tab "Cập nhật hồ sơ".
     */
    @PutMapping("/{id}")
    public ApiResponse<MedicalRecordResponse> updateInfo(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePatientRequest request) {

        return ApiResponse.<MedicalRecordResponse>builder()
                .result(medicalRecordService.updatePatientInfo(id, request))
                .message("Cập nhật thông tin bệnh nhân thành công!")
                .build();
    }

    /**
     * API Kê đơn/Chỉ định tiêm chủng mới.
     * Dùng cho Tab "Kê đơn".
     */
    @PostMapping("/{id}/prescribe")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ApiResponse<String> prescribe(
            @PathVariable UUID id,
            @RequestBody @Valid PrescribeRequest request) {

        medicalRecordService.prescribeVaccine(id, request);
        return ApiResponse.<String>builder()
                .result("Chỉ định tiêm chủng đã được ghi nhận thành công.")
                .build();
    }

    @PostMapping("/confirm-injection/{maDangKy}")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ApiResponse<String> confirmInjection(
            @PathVariable UUID maDangKy,
            @RequestBody Map<String, String> request) {

        String phanUng = request.get("phanUngSauTiem");
        String tacDung = request.get("thoiGianTacDung"); // Lấy từ UI nếu có

        medicalRecordService.confirmInjection(maDangKy, phanUng, tacDung);

        return ApiResponse.<String>builder()
                .result("Xác nhận tiêm chủng thành công")
                .build();
    }
}
