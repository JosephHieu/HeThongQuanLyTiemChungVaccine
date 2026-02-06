package com.josephhieu.vaccinebackend.modules.medical.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.PrescribeRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdatePatientRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdateProfileRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.MedicalRecordResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.PatientProfileResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.VaccinationHistoryResponse;
import com.josephhieu.vaccinebackend.modules.medical.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller cung cấp các API liên quan đến Hồ sơ bệnh án và Quản lý sức khỏe cá nhân.
 * Hỗ trợ hai phân hệ chính:
 * 1. Phân hệ Nhân viên: Quản lý, điều phối và xác nhận tiêm chủng cho bệnh nhân.
 * 2. Phân hệ Bệnh nhân (Self-service): Tự quản lý hồ sơ và xem lịch sử tiêm chủng cá nhân.
 *
 * @author Joseph Hieu
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medical")
public class MedicalController {

    private final MedicalRecordService medicalRecordService;

    // ========================================================================
    // PHÂN HỆ QUẢN TRỊ & NHÂN VIÊN (STAFF & ADMIN PORTAL)
    // ========================================================================

    /**
     * Truy xuất hồ sơ bệnh án chi tiết của một bệnh nhân theo ID.
     * Dùng cho giao diện Điều phối/Khám sàng lọc của Nhân viên y tế.
     *
     * @param id Mã định danh bệnh nhân.
     * @return ApiResponse chứa dữ liệu hồ sơ bệnh án.
     */
    @GetMapping("/records/{id}")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ApiResponse<MedicalRecordResponse> getRecord(@PathVariable UUID id) {
        return ApiResponse.<MedicalRecordResponse>builder()
                .result(medicalRecordService.getMedicalRecord(id))
                .build();
    }

    /**
     * Cập nhật thông tin hành chính bệnh nhân từ phía nhân viên.
     *
     * @param id Mã định danh bệnh nhân cần cập nhật.
     * @param request DTO chứa các thông tin thay đổi.
     * @return ApiResponse chứa thông tin sau khi cập nhật.
     */
    @PutMapping("/records/{id}")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ApiResponse<MedicalRecordResponse> updateInfo(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePatientRequest request) {
        return ApiResponse.<MedicalRecordResponse>builder()
                .result(medicalRecordService.updatePatientInfo(id, request))
                .message("Cập nhật thông tin bệnh nhân thành công!")
                .build();
    }

    /**
     * Ghi nhận chỉ định tiêm chủng (Kê đơn) từ bác sĩ khám sàng lọc.
     */
    @PostMapping("/records/{id}/prescribe")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ApiResponse<String> prescribe(
            @PathVariable UUID id,
            @RequestBody @Valid PrescribeRequest request) {
        medicalRecordService.prescribeVaccine(id, request);
        return ApiResponse.<String>builder()
                .result("Chỉ định tiêm chủng đã được ghi nhận thành công.")
                .build();
    }

    /**
     * Xác nhận hoàn thành quy trình tiêm thực tế và lưu vào Hồ sơ bệnh án.
     */
    @PostMapping("/records/confirm-injection/{maDangKy}")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ApiResponse<String> confirmInjection(
            @PathVariable UUID maDangKy,
            @RequestBody Map<String, String> request) {
        String phanUng = request.get("phanUngSauTiem");
        String tacDung = request.get("thoiGianTacDung");
        medicalRecordService.confirmInjection(maDangKy, phanUng, tacDung);
        return ApiResponse.<String>builder()
                .result("Xác nhận hoàn thành mũi tiêm và tạo hồ sơ bệnh án thành công.")
                .build();
    }

    // ========================================================================
    // PHÂN HỆ BỆNH NHÂN (PATIENT SELF-SERVICE)
    // ========================================================================

    /**
     * Truy xuất thông tin cá nhân của chính người dùng đang đăng nhập.
     * Identity được xác thực qua JWT Token, không cần truyền ID trên URL.
     */
    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<PatientProfileResponse> getMyProfile() {

        return ApiResponse.<PatientProfileResponse>builder()
                .result(medicalRecordService.getMyProfile())
                .build();
    }

    /**
     * Cho phép bệnh nhân tự cập nhật thông tin cá nhân của chính mình.
     */
    @PutMapping("/my-profile")
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<PatientProfileResponse> updateMyProfile(
            @RequestBody @Valid UpdateProfileRequest request) {

        return ApiResponse.<PatientProfileResponse>builder()
                .result(medicalRecordService.updateMyProfile(request))
                .message("Cập nhật hồ sơ cá nhân thành công!")
                .build();
    }

    /**
     * Lấy toàn bộ danh sách lịch sử các mũi đã tiêm của bệnh nhân.
     * Dữ liệu dùng để hiển thị lên bảng Lịch sử tiêm chủng tại trang Profile.
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('Normal User Account')")
    public ApiResponse<List<VaccinationHistoryResponse>> getMyHistory() {

        return ApiResponse.<List<VaccinationHistoryResponse>>builder()
                .result(medicalRecordService.getMyVaccinationHistory())
                .build();
    }


}
