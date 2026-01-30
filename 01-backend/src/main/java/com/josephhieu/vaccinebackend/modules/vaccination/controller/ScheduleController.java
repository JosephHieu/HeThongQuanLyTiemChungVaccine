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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller quản lý các yêu cầu liên quan đến Lịch tiêm chủng.
 * Tiếp nhận dữ liệu từ màn hình điều chỉnh lịch tiêm.
 */
@RestController
@RequestMapping("api/v1/vaccination/schedules")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Lấy chi tiết lịch tiêm theo một ngày cụ thể.
     * Dùng để đổ dữ liệu vào ScheduleForm khi người dùng click vào CalendarSidebar.
     */
    @GetMapping("/by-date")
    public ApiResponse<ScheduleResponse> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String shift) {

        log.info("Truy vấn lịch tiêm: Ngày {} - Ca {}", date, shift);
        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.getScheduleByDateAndShift(date, shift))
                .build();
    }

    /**
     * Lấy danh sách các ngày có lịch tiêm trong một khoảng thời gian.
     * Dùng để hiển thị các "dấu chấm xanh" báo hiệu có lịch trên CalendarSidebar.
     */
    @GetMapping("/active-dates")
    public ApiResponse<List<LocalDate>> getActiveDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ApiResponse.<List<LocalDate>>builder()
                .result(scheduleService.getActiveDatesInPeriod(start, end))
                .build();
    }

    /**
     * Tạo mới lịch tiêm chủng.
     */
    @PostMapping
    public ApiResponse<ScheduleResponse> createSchedule(@RequestBody @Valid ScheduleCreationRequest request) {

        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.createScheduleService(request))
                .message("Tạo lịch tiêm chủng thành công")
                .build();
    }

    /**
     * Cập nhật lịch tiêm chủng hiện có.
     * Tương ứng với hành động "Lưu lại" sau khi chỉnh sửa trên giao diện.
     */
    @PutMapping("/{id}")
    public ApiResponse<ScheduleResponse> updateSchedule(
            @PathVariable UUID id,
            @RequestBody @Valid ScheduleCreationRequest request) {

        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.updateSchedule(id, request))
                .message("Cập nhật lịch tiêm thành công")
                .build();
    }

    /**
     * Xóa lịch tiêm chủng.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSchedule(@PathVariable UUID id) {
        scheduleService.deleteSchedule(id);
        return ApiResponse.<Void>builder()
                .message("Xóa lịch tiêm thành công")
                .build();
    }

    /**
     * Lấy toàn bộ danh sách lịch tiêm (hỗ trợ phân trang và tìm kiếm).
     * Dùng cho các màn hình quản lý dạng bảng (List view).
     */
    @GetMapping
    public ApiResponse<PageResponse<ScheduleResponse>> getAllSchedules(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ApiResponse.<PageResponse<ScheduleResponse>>builder()
                .result(scheduleService.getAllSchedules(page, size, search, start, end))
                .build();
    }

    /**
     * Lấy danh sách bệnh nhân đăng ký theo NGÀY.
     * Giúp UI gọi trực tiếp khi click vào Calendar mà không cần biết mã UUID của lịch.
     */
    @GetMapping("/registrations-by-date")
    public ApiResponse<PageResponse<RegistrationResponse>> getRegistrationsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<RegistrationResponse>>builder()
                .result(scheduleService.getRegistrationsByDate(date, page, size))
                .build();
    }

    /**
     * Lấy danh sách các lô vắc-xin khả dụng trong kho.
     * Dùng để đổ dữ liệu vào ô Select (Dropdown) trên ScheduleForm.
     */
    @GetMapping("/available-batches")
    public ApiResponse<List<BatchSummaryResponse>> getAvailableBatches() {

        log.info("Truy vấn danh sách lô vắc-xin khả dụng cho lịch tiêm");
        return ApiResponse.<List<BatchSummaryResponse>>builder()
                .result(scheduleService.getAvailableBatches())
                .build();
    }

}
