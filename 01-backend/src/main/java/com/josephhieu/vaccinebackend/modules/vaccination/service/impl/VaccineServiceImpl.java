package com.josephhieu.vaccinebackend.modules.vaccination.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.LichTiemChung;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.LichTiemChungRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.service.VaccineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccineServiceImpl implements VaccineService {

    private final VacXinRepository vacXinRepository;
    private final ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    private final LichTiemChungRepository lichTiemChungRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final LoVacXinRepository loVacXinRepository;

    @Override
    public Page<VaccineInfoResponse> getVaccines(VaccineSearchRequest request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return vacXinRepository.searchVaccines(request.getKeyword(), pageable);
    }

    @Override
    @Transactional
    public void registerVaccination(VaccinationRegistrationRequest request) {

        // 1. Xác định "Ai" đang đăng ký (Lấy từ JWT Token đã được filter xác thực)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BenhNhan patient = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Kiểm tra lịch tiêm chủng có tồn tại không
        LichTiemChung schedule = lichTiemChungRepository.findById(request.getMaLichTiemChung())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 3. Tự động tìm 1 lô vắc-xin còn hàng cho loại vắc-xin người dùng chọn
        LoVacXin availableBatch = loVacXinRepository.findFirstByVacXin_MaVacXinAndSoLuongGreaterThanOrderByNgayNhanAsc(
                        request.getMaVacXin(), 0)
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_OUT_OF_STOCK));

        // 4. Tạo bản ghi đăng ký tiêm chủng
        ChiTietDangKyTiem registration = ChiTietDangKyTiem.builder()
                .benhNhan(patient)
                .loVacXin(availableBatch)
                .lichTiemChung(schedule)
                .thoiGianCanTiem(schedule.getNgayTiem()) // Lấy ngày từ lịch tiêm chủng làm ngày hẹn
                .ghiChu(request.getGhiChu())
                .build();

        chiTietDangKyTiemRepository.save(registration);

        log.info("Đăng ký thành công cho bệnh nhân: {} - Vắc-xin: {}",
                patient.getTenBenhNhan(), availableBatch.getVacXin().getTenVacXin());
    }

}
