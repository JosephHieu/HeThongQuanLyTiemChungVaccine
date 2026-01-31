package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import org.springframework.data.domain.Page;

public interface VaccineService {

    /**
     * Tra cứu danh sách vắc xin dựa trên từ khóa và phân trang
     */
    Page<VaccineInfoResponse> getVaccines(VaccineSearchRequest request);

    void registerVaccination(VaccinationRegistrationRequest request);
}


