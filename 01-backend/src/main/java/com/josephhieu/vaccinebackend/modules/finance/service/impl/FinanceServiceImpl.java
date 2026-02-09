package com.josephhieu.vaccinebackend.modules.finance.service.impl;

import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.VaccineFullResponse;
import com.josephhieu.vaccinebackend.modules.finance.service.FinanceService;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoaiVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceServiceImpl implements FinanceService {

    private final VacXinRepository vacXinRepository;
    private final LoaiVacXinRepository loaiVacXinRepository;
    private final LoVacXinRepository loVacXinRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<VacXin> vacXinPage = vacXinRepository.findAll(pageable);

        List<VaccineFullResponse> data = vacXinPage.getContent().stream()
                .map(this::mapToFullResponse)
                .toList();

        return PageResponse.<VaccineFullResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(vacXinPage.getTotalPages())
                .totalElements(vacXinPage.getTotalElements())
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public VaccineFullResponse createVaccine(VaccineFullRequest request) {
        LoaiVacXin loai = loaiVacXinRepository.findById(request.getMaLoaiVacXin())
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        VacXin newVacXin = VacXin.builder()
                .tenVacXin(request.getTenVacXin())
                .loaiVacXin(loai)
                .hanSuDung(request.getHanSuDung())
                .hamLuong(request.getHamLuong())
                .phongNguaBenh(request.getPhongNguaBenh())
                .doTuoiTiemChung(request.getDoTuoiTiemChung())
                .donGia(request.getDonGia())
                .dieuKienBaoQuan(request.getDieuKienBaoQuan())
                .build();

        log.info("Thêm mới vắc-xin vào danh mục: {}", request.getTenVacXin());
        return mapToFullResponse(vacXinRepository.save(newVacXin));
    }

    @Override
    @Transactional
    public VaccineFullResponse updateVaccine(UUID id, VaccineFullRequest request) {
        VacXin vacXin = vacXinRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        LoaiVacXin loai = loaiVacXinRepository.findById(request.getMaLoaiVacXin())
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        // Mapping dữ liệu từ Request sang Entity
        vacXin.setTenVacXin(request.getTenVacXin());
        vacXin.setLoaiVacXin(loai);
        vacXin.setHanSuDung(request.getHanSuDung());
        vacXin.setHamLuong(request.getHamLuong());
        vacXin.setPhongNguaBenh(request.getPhongNguaBenh());
        vacXin.setDoTuoiTiemChung(request.getDoTuoiTiemChung());
        vacXin.setDonGia(request.getDonGia());
        vacXin.setDieuKienBaoQuan(request.getDieuKienBaoQuan());

        log.info("Cập nhật toàn diện vắc-xin ID: {}", id);
        return mapToFullResponse(vacXinRepository.save(vacXin));
    }

    @Override
    @Transactional
    public void deleteVaccine(UUID id) {
        if (!vacXinRepository.existsById(id)) {
            throw new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND);
        }

        // Ràng buộc nghiệp vụ: Không xóa nếu đã có lô vắc-xin (tránh mồ côi dữ liệu)
        if (loVacXinRepository.existsByVacXin_MaVacXin(id)) {
            throw new AppException(ErrorCode.INVALID_INFO);
        }

        vacXinRepository.deleteById(id);
        log.warn("Đã xóa vắc-xin ID: {} khỏi hệ thống", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalInventoryValue() {
        BigDecimal total = loVacXinRepository.getTotalInventoryValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public VaccineFullResponse getVaccineDetail(UUID id) {
        return vacXinRepository.findById(id)
                .map(this::mapToFullResponse)
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size, String keyword) {
        // Spring Data JPA dùng page bắt đầu từ 0
        Pageable pageable = PageRequest.of(page - 1, size);

        // Gọi Repository mới với keyword
        Page<VacXin> vacXinPage = vacXinRepository.findAllByKeyword(keyword, pageable);

        List<VaccineFullResponse> data = vacXinPage.getContent().stream()
                .map(this::mapToFullResponse)
                .toList();

        return PageResponse.<VaccineFullResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(vacXinPage.getTotalPages())
                .totalElements(vacXinPage.getTotalElements())
                .data(data)
                .build();
    }

    /**
     * Chuyển đổi Entity VacXin sang VaccineFullResponse DTO.
     * Tự động lấy danh tính nhân viên đang thao tác để gắn vào Audit Info.
     */
    private VaccineFullResponse mapToFullResponse(VacXin v) {
        String currentStaff = SecurityContextHolder.getContext().getAuthentication().getName();

        return VaccineFullResponse.builder()
                .maVacXin(v.getMaVacXin())
                .tenVacXin(v.getTenVacXin())
                .maLoaiVacXin(v.getLoaiVacXin() != null ? v.getLoaiVacXin().getMaLoaiVacXin() : null)
                .tenLoaiVacXin(v.getLoaiVacXin() != null ? v.getLoaiVacXin().getTenLoaiVacXin() : "N/A")
                .hanSuDung(v.getHanSuDung())
                .hamLuong(v.getHamLuong())
                .phongNguaBenh(v.getPhongNguaBenh())
                .doTuoiTiemChung(v.getDoTuoiTiemChung())
                .dieuKienBaoQuan(v.getDieuKienBaoQuan())
                .donGia(v.getDonGia())
                .ngayCapNhat(java.time.LocalDateTime.now())
                .nguoiCapNhat(currentStaff != null ? currentStaff : "System Admin")
                .build();
    }
}