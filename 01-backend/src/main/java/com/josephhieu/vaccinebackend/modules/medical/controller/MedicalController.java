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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller quản lý chuyên môn y tế, hồ sơ bệnh án và sức khỏe cá nhân.
 * <p>
 * Hệ thống cung cấp hai phân hệ tương tác độc lập:
 * 1. Quản trị & Nhân viên: Thực hiện các nghiệp vụ khám sàng lọc, chỉ định tiêm và xác nhận hồ sơ y tế.
 * 2. Bệnh nhân (Self-service): Cho phép bệnh nhân chủ động quản lý thông tin cá nhân và tra cứu lịch sử tiêm chủng.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medical")
@Slf4j
public class MedicalController {

    private final MedicalRecordService medicalRecordService;

    // ========================================================================
    // PHÂN HỆ QUẢN TRỊ & NHÂN VIÊN (STAFF & ADMIN PORTAL)
    // ========================================================================

    /**
     * Truy xuất thông tin hồ sơ bệnh án chi tiết của bệnh nhân.
     * Phục vụ trực tiếp cho nhân viên y tế trong quá trình điều phối hoặc khám sàng lọc trước tiêm.
     *
     * @param id Mã định danh duy nhất của bệnh nhân.
     * @return {@link ResponseEntity} chứa dữ liệu hồ sơ bệnh án chi tiết.
     */
    @GetMapping("/records/{id}")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getRecord(@PathVariable UUID id) {
        log.info("Nhân viên y tế truy xuất hồ sơ bệnh án của bệnh nhân ID: {}", id);
        MedicalRecordResponse result = medicalRecordService.getMedicalRecord(id);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Cập nhật thông tin hành chính và tiền sử y tế của bệnh nhân bởi nhân viên quản lý.
     *
     * @param id Mã định danh bệnh nhân cần chỉnh sửa.
     * @param request Dữ liệu cập nhật mới từ phía quản trị.
     * @return {@link ResponseEntity} chứa hồ sơ bệnh án sau khi đã cập nhật thành công.
     */
    @PutMapping("/records/{id}")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> updateInfo(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePatientRequest request) {

        log.info("Cập nhật thông tin y tế cho bệnh nhân ID: {}", id);
        MedicalRecordResponse result = medicalRecordService.updatePatientInfo(id, request);

        return ResponseEntity.ok(ApiResponse.success(result, "Cập nhật thông tin bệnh nhân thành công!"));
    }

    /**
     * Ghi nhận chỉ định tiêm chủng (kê đơn) dựa trên kết quả khám sàng lọc của bác sĩ.
     * <p>
     * Thao tác này là điều kiện bắt buộc để bệnh nhân có thể tiến hành tiêm chủng thực tế.
     * </p>
     *
     * @param id Mã định danh bệnh nhân.
     * @param request Thông tin loại vắc-xin và liều lượng được chỉ định.
     * @return {@link ResponseEntity} với mã 201 (Created) xác nhận chỉ định đã được lưu.
     */
    @PostMapping("/records/{id}/prescribe")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ResponseEntity<ApiResponse<String>> prescribe(
            @PathVariable UUID id,
            @RequestBody @Valid PrescribeRequest request) {

        log.info("Bác sĩ thực hiện chỉ định tiêm chủng cho bệnh nhân ID: {}", id);
        medicalRecordService.prescribeVaccine(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Chỉ định tiêm chủng đã được ghi nhận thành công."));
    }

    /**
     * Xác nhận hoàn thành quy trình tiêm thực tế và ghi nhận vào lịch sử hồ sơ bệnh án điện tử.
     * <p>
     * Hệ thống đồng thời lưu trữ thông tin về phản ứng sau tiêm và dự báo thời gian tác dụng của vắc-xin.
     * </p>
     *
     * @param maDangKy Mã lượt đăng ký tiêm chủng cần xác nhận.
     * @param request Chứa thông tin phản ứng sau tiêm và thời gian tác dụng.
     * @return {@link ResponseEntity} với mã 201 (Created) xác nhận mũi tiêm đã hoàn tất.
     */
    @PostMapping("/records/confirm-injection/{maDangKy}")
    @PreAuthorize("hasRole('Administrator') or hasRole('Nhân viên y tế')")
    public ResponseEntity<ApiResponse<String>> confirmInjection(
            @PathVariable UUID maDangKy,
            @RequestBody Map<String, String> request) {

        String phanUng = request.get("phanUngSauTiem");
        String tacDung = request.get("thoiGianTacDung");

        log.info("Xác nhận hoàn thành mũi tiêm cho lượt đăng ký: {}", maDangKy);
        medicalRecordService.confirmInjection(maDangKy, phanUng, tacDung);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Xác nhận hoàn thành mũi tiêm và tạo hồ sơ bệnh án thành công."));
    }

    // ========================================================================
    // PHÂN HỆ BỆNH NHÂN (PATIENT SELF-SERVICE)
    // ========================================================================

    /**
     * Truy xuất thông tin hồ sơ cá nhân của bệnh nhân đang đăng nhập hệ thống.
     * Dữ liệu được xác thực an toàn thông qua định danh tài khoản (Identity) từ Security Context.
     *
     * @return {@link ResponseEntity} chứa thông tin hồ sơ bệnh nhân hiện tại.
     */
    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<PatientProfileResponse>> getMyProfile() {
        log.info("Người dùng đang truy cập hồ sơ cá nhân tự phục vụ.");
        PatientProfileResponse result = medicalRecordService.getMyProfile();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Cho phép bệnh nhân chủ động cập nhật các thông tin liên lạc và hồ sơ cá nhân.
     *
     * @param request Dữ liệu hồ sơ mới từ người dùng.
     * @return {@link ResponseEntity} chứa thông tin hồ sơ sau cập nhật.
     */
    @PutMapping("/my-profile")
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<PatientProfileResponse>> updateMyProfile(
            @RequestBody @Valid UpdateProfileRequest request) {

        log.info("Người dùng thực hiện cập nhật hồ sơ cá nhân.");
        PatientProfileResponse result = medicalRecordService.updateMyProfile(request);

        return ResponseEntity.ok(ApiResponse.success(result, "Cập nhật hồ sơ cá nhân thành công!"));
    }

    /**
     * Truy xuất toàn bộ lịch sử các mũi tiêm đã thực hiện trong quá khứ của bệnh nhân.
     * Dữ liệu phục vụ việc theo dõi sức khỏe và cung cấp bằng chứng tiêm chủng điện tử.
     *
     * @return {@link ResponseEntity} chứa danh sách lịch sử tiêm chủng cá nhân.
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('Normal User Account')")
    public ResponseEntity<ApiResponse<List<VaccinationHistoryResponse>>> getMyHistory() {
        log.info("Người dùng tra cứu lịch sử tiêm chủng cá nhân.");
        List<VaccinationHistoryResponse> result = medicalRecordService.getMyVaccinationHistory();

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
