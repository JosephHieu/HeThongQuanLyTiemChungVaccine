package com.josephhieu.vaccinebackend.modules.vaccination.service.impl;

import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.VaccineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccineServiceImpl implements VaccineService {

    private final VacXinRepository vacXinRepository;

    @Override
    public Page<VaccineInfoResponse> getVaccines(VaccineSearchRequest request) {

        log.info("Searching vaccines with keyword: {}", request.getKeyword());

        String keyword = request.getKeyword() != null ? request.getKeyword().trim() : "";

        // Xử lý logic phân trang: Chuyển đổi từ page 1 (User gửi) về page 0 (Spring Data JPA)
        int pageIndex = Math.max(0, request.getPage() - 1);
        Pageable pageable = PageRequest.of(pageIndex, request.getSize());

        // Gọi repository đã tối ưu câu lệnh SUM để tính tồn kho thực tế
        return vacXinRepository.searchVaccines(keyword, pageable);
    }

    @Override
    @Transactional
    public void registerVaccination(VaccinationRegistrationRequest request) {


    }


}
