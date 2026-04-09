package com.josephhieu.vaccinebackend.modules.vaccination.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationHistoryResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.VaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller chịu trách nhiệm điều phối các luồng nghiệp vụ tiêm chủng dành cho khách hàng.
 * <p>
 * Cung cấp các giao diện lập trình (API) để tra cứu thông tin vắc-xin, quản lý quy trình
 * đăng ký tiêm chủng cá nhân và theo dõi lịch sử tiêm chủng dựa trên hồ sơ bệnh nhân.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/vaccinations")
@RequiredArgsConstructor
@Slf4j
public class VaccineController {

    private final VaccineService vaccineService;

    /**
     * Truy vấn danh sách vắc-xin hiện có trong hệ thống với các bộ lọc linh hoạt.
     * * @param request Đối tượng chứa các tiêu chí lọc (tên, loại vắc-xin, độ tuổi phù hợp) và thông tin phân trang.
     * @return {@link ResponseEntity} bọc {@link ApiResponse} chứa trang dữ liệu vắc-xin kết quả.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('Normal User Account', 'Administrator', 'Nhân viên y tế')")
    public ResponseEntity<ApiResponse<Page<VaccineInfoResponse>>> getVaccines(@Valid VaccineSearchRequest request) {
        log.info("Bắt đầu truy vấn danh sách vắc-xin với tiêu chí: {}", request);
        Page<VaccineInfoResponse> result = vaccineService.getVaccines(request);

        return ResponseEntity.ok(ApiResponse.success(result, "Tải danh sách vắc-xin thành công"));
    }

    /**
     * Tiếp nhận và xử lý yêu cầu đăng ký tiêm chủng mới từ người dùng.
     * <p>
     * Phương thức này thực hiện xác thực thông tin bệnh nhân và kiểm tra tính khả dụng
     * của vắc-xin trước khi ghi nhận lượt đăng ký vào cơ sở dữ liệu.
     * </p>
     *
     * @param request Thông tin chi tiết về lượt đăng ký tiêm chủng.
     * @return {@link ResponseEntity} với mã trạng thái 201 (Created) khi đăng ký thành công.
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('Normal User Account')")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid VaccinationRegistrationRequest request) {

        vaccineService.registerVaccination(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tiêm thành công", "Thông tin lượt tiêm đã được hệ thống ghi nhận."));
    }

    /**
     * Truy xuất lịch sử đăng ký tiêm chủng của cá nhân dựa trên định danh tài khoản hiện tại.
     *
     * @return {@link ResponseEntity} chứa danh sách chi tiết các mũi tiêm đã đăng ký hoặc đã hoàn thành.
     */
    @GetMapping("/my-registrations")
    @PreAuthorize("hasAnyAuthority('Normal User Account')")
    public ResponseEntity<ApiResponse<List<RegistrationHistoryResponse>>> getMyRegistrations() {
        log.info("Truy xuất lịch sử đăng ký cá nhân.");
        List<RegistrationHistoryResponse> result = vaccineService.getMyRegistrations();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Xử lý yêu cầu hủy bỏ lượt đăng ký tiêm chủng đã được đặt trước.
     * <p>
     * Hệ thống sẽ tự động thực hiện các tác vụ hoàn kho (Rollback inventory) và
     * cập nhật trạng thái hóa đơn liên quan để đảm bảo tính toàn vẹn dữ liệu.
     * </p>
     *
     * @param maDangKy Mã định danh duy nhất (UUID) của bản ghi đăng ký cần hủy.
     * @return {@link ResponseEntity} xác nhận thao tác hủy đã hoàn tất.
     */
    @PostMapping("/cancel/{maDangKy}")
    @PreAuthorize("hasAnyAuthority('Normal User Account')")
    public ResponseEntity<ApiResponse<String>> cancel(@PathVariable UUID maDangKy) {
        log.warn("Thực hiện lệnh hủy lượt đăng ký tiêm: {}", maDangKy);
        vaccineService.cancelRegistration(maDangKy);

        return ResponseEntity.ok(ApiResponse.success(
                "Hủy đăng ký thành công và đã hoàn lại vắc-xin vào kho",
                "Giao dịch đã được thu hồi."
        ));
    }

}
