package com.josephhieu.vaccinebackend.modules.vaccination.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.VaccineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vaccines")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService vaccineService;

    @GetMapping
    public ApiResponse<Page<VaccineInfoResponse>> getVaccines(@ModelAttribute VaccineSearchRequest request) {

        return ApiResponse.<Page<VaccineInfoResponse>>builder()
                .result(vaccineService.getVaccines(request))
                .build();
    }

}
