package com.josephhieu.vaccinebackend.modules.vaccination.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.BatchSummaryResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.ScheduleResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller chịu trách nhiệm quản lý kế hoạch và lịch tiêm chủng của trung tâm.
 * <p>
 * Cung cấp các giao diện lập trình (API) phục vụ việc điều phối lịch tiêm,
 * hiển thị trạng thái trên lịch (Calendar) và quản lý danh sách đăng ký theo lịch.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("api/v1/vaccination/schedules")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Truy xuất thông tin chi tiết lịch tiêm theo ngày và ca làm việc cụ thể.
     * Dữ liệu này thường được dùng để hiển thị thông tin chi tiết khi người dùng chọn một ngày trên Calendar.
     *
     * @param date Ngày cần truy vấn lịch (Định dạng ISO: yyyy-MM-dd).
     * @param shift Ca làm việc (Sáng/Chiều).
     * @return {@link ResponseEntity} chứa thông tin chi tiết ca tiêm.
     */
    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String shift) {

        log.info("Truy vấn chi tiết lịch tiêm: Ngày {} - Ca {}", date, shift);
        ScheduleResponse result = scheduleService.getScheduleByDateAndShift(date, shift);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Lấy danh sách các ngày có lịch tiêm chủng đang hoạt động trong một khoảng thời gian.
     * Phục vụ tính năng hiển thị các dấu hiệu nhận biết (indicators) trên giao diện lịch.
     *
     * @param start Ngày bắt đầu khoảng thời gian.
     * @param end Ngày kết thúc khoảng thời gian.
     * @return {@link ResponseEntity} chứa danh sách các đối tượng {@link LocalDate}.
     */
    @GetMapping("/active-dates")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getActiveDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        log.info("Truy vấn các ngày có lịch tiêm từ {} đến {}", start, end);
        List<LocalDate> result = scheduleService.getActiveDatesInPeriod(start, end);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Thiết lập một lịch tiêm chủng mới trên hệ thống.
     * Hành động này yêu cầu chỉ định lô vắc-xin, số lượng dự kiến và thời gian cụ thể.
     *
     * @param request Thông tin khởi tạo lịch tiêm.
     * @return {@link ResponseEntity} với mã 201 (Created) và thông tin lịch vừa tạo.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(@RequestBody @Valid ScheduleCreationRequest request) {
        log.info("Yêu cầu tạo mới lịch tiêm chủng cho ngày: {}", request.getNgayTiem());
        ScheduleResponse result = scheduleService.createScheduleService(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Tạo lịch tiêm chủng thành công"));
    }

    /**
     * Cập nhật thông tin của một lịch tiêm chủng đã tồn tại.
     *
     * @param id Định danh duy nhất (UUID) của lịch tiêm.
     * @param request Thông tin cập nhật mới.
     * @return {@link ResponseEntity} chứa thông tin sau khi cập nhật.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @PathVariable UUID id,
            @RequestBody @Valid ScheduleCreationRequest request) {

        log.info("Cập nhật lịch tiêm chủng ID: {}", id);
        ScheduleResponse result = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(ApiResponse.success(result, "Cập nhật lịch tiêm thành công"));
    }

    /**
     * Loại bỏ một lịch tiêm chủng khỏi hệ thống dựa trên ID.
     *
     * @param id Định danh duy nhất của lịch tiêm cần xóa.
     * @return {@link ResponseEntity} xác nhận thao tác xóa thành công.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable UUID id) {
        log.warn("Thực hiện xóa lịch tiêm chủng ID: {}", id);
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa lịch tiêm thành công"));
    }

    /**
     * Liệt kê danh sách toàn bộ lịch tiêm chủng với khả năng tìm kiếm và phân trang.
     * Thường được sử dụng cho các màn hình quản trị dạng bảng (Table view).
     *
     * @param page Chỉ số trang (bắt đầu từ 1).
     * @param size Số lượng bản ghi trên mỗi trang.
     * @param search Từ khóa tìm kiếm theo tên vắc-xin hoặc địa điểm.
     * @param start Lọc theo ngày bắt đầu (Tùy chọn).
     * @param end Lọc theo ngày kết thúc (Tùy chọn).
     * @return {@link ResponseEntity} chứa trang dữ liệu lịch tiêm.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ScheduleResponse>>> getAllSchedules(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        log.info("Tải danh sách lịch tiêm phân trang - Trang: {}, Kích thước: {}", page, size);
        PageResponse<ScheduleResponse> result = scheduleService.getAllSchedules(page, size, search, start, end);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Truy xuất danh sách bệnh nhân đã đăng ký tiêm chủng theo một ngày cụ thể.
     * Giúp điều phối viên kiểm soát danh sách người tiêm trong ngày mà không cần tra cứu theo ID lịch.
     *
     * @param date Ngày cần lấy danh sách đăng ký.
     * @param page Chỉ số trang.
     * @param size Số lượng bản ghi mỗi trang.
     * @return {@link ResponseEntity} chứa trang danh sách đăng ký.
     */
    @GetMapping("/registrations-by-date")
    public ResponseEntity<ApiResponse<PageResponse<RegistrationResponse>>> getRegistrationsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Lấy danh sách bệnh nhân đăng ký cho ngày: {}", date);
        PageResponse<RegistrationResponse> result = scheduleService.getRegistrationsByDate(date, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Lấy danh sách các lô vắc-xin hiện có và đủ điều kiện để đưa vào lịch tiêm (Còn hạn, còn tồn).
     * Cung cấp dữ liệu cho các thành phần lựa chọn (Dropdown) trên giao diện tạo lịch.
     *
     * @return {@link ResponseEntity} chứa danh sách tóm tắt các lô khả dụng.
     */
    @GetMapping("/available-batches")
    public ResponseEntity<ApiResponse<List<BatchSummaryResponse>>> getAvailableBatches() {
        log.info("Truy vấn danh sách lô vắc-xin khả dụng để thiết lập lịch tiêm.");
        List<BatchSummaryResponse> result = scheduleService.getAvailableBatches();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Cung cấp danh sách các lịch tiêm đang mở cho phép người dùng đăng ký.
     * Tuân thủ quy trình nghiệp vụ tra cứu lịch tiêm công khai của Bệnh nhân.
     *
     * @return {@link ResponseEntity} chứa danh sách các lịch tiêm khả dụng cho người dùng.
     */
    @GetMapping("/opening")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getOpeningSchedules() {
        log.info("Bệnh nhân thực hiện tra cứu các lịch tiêm đang mở đăng ký.");
        List<ScheduleResponse> result = scheduleService.getOpeningSchedulesForUser();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
