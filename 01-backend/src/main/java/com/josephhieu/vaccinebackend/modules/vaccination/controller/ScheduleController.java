package com.josephhieu.vaccinebackend.modules.vaccination.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.ScheduleResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * API tạo mới một lịch tiêm chủng và phân công bác sĩ trực.
     * Tương ứng với hành động nhấn nút "Save" trên giao diện.
     *
     * @param request DTO chứa thông tin lịch tiêm và danh sách ID bác sĩ.
     * @return ApiResponse chứa thông tin lịch tiêm đã được tạo thành công.
     */
    @PostMapping
    public ApiResponse<ScheduleResponse> createSchedule(@RequestBody @Valid ScheduleCreationRequest request) {

        log.info("Tiếp nhận yêu cầu tạo lịch tiêm vào ngày: {}", request.getNgayTiem());

        ScheduleResponse response = scheduleService.createScheduleService(request);

        return ApiResponse.<ScheduleResponse>builder()
                .result(response)
                .message("Tạo lịch tiêm chủng thành công")
                .build();
    }
}
