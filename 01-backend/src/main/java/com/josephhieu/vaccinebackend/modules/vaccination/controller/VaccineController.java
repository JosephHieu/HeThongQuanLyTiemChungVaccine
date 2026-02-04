package com.josephhieu.vaccinebackend.modules.vaccination.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationHistoryResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.VaccineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vaccinations")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService vaccineService;

    /**
     * API Tra cứu vắc-xin có phân trang cho người dùng
     * Endpoint: GET /api/v1/vaccinations
     */
    @GetMapping
    public ApiResponse<Page<VaccineInfoResponse>> getVaccines(@Valid VaccineSearchRequest request) {

        return ApiResponse.<Page<VaccineInfoResponse>>builder()
                .result(vaccineService.getVaccines(request))
                .build();
    }

    /**
     * API Đăng ký tiêm chủng
     * Endpoint: POST /api/v1/vaccinations/register
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid VaccinationRegistrationRequest request) {

        vaccineService.registerVaccination(request);
        return ApiResponse.<String>builder()
                .result("Đăng ký tiêm thành công")
                .build();
    }

    /**
     * API Lấy lịch sử đăng ký của người dùng hiện tại
     * Endpoint: GET /api/v1/vaccinations/my-registrations
     */
    @GetMapping("/my-registrations")
    public ApiResponse<List<RegistrationHistoryResponse>> getMyRegistrations() {

        return ApiResponse.<List<RegistrationHistoryResponse>>builder()
                .result(vaccineService.getMyRegistrations())
                .build();
    }

    /**
     * API Hủy đăng ký tiêm
     * Endpoint: POST /api/v1/vaccinations/cancel/{maDangKy}
     */
    @PostMapping("/cancel/{maDangKy}")
    public ApiResponse<String> cancel(@PathVariable UUID maDangKy) {

        vaccineService.cancelRegistration(maDangKy);
        return ApiResponse.<String>builder()
                .result("Hủy đăng ký thành công và đã hoàn lại vắc-xin vào kho")
                .build();
    }

}
