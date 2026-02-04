package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationHistoryResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface VaccineService {

    /**
     * Tra cứu danh sách vắc xin dựa trên từ khóa và phân trang
     */
    Page<VaccineInfoResponse> getVaccines(VaccineSearchRequest request);

    void registerVaccination(VaccinationRegistrationRequest request);

    List<RegistrationHistoryResponse> getMyRegistrations();

    void cancelRegistration(UUID maDangKy);
}


