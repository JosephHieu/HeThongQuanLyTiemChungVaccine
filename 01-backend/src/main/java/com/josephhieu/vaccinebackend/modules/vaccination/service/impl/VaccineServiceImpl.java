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

       // 1. Xác định "Ai" đang đăng ký
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BenhNhan patient = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Kiểm tra lịch tiêm chủng
        LichTiemChung schedule = lichTiemChungRepository.findById(request.getMaLichTiemChung())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 3. Kiểm tra trùng lặp: Người này đã đăng ký lịch này chưa?
        boolean isAlreadyRegistered = chiTietDangKyTiemRepository
                .existsByBenhNhan_MaBenhNhanAndLichTiemChung_MaLichTiem(patient.getMaBenhNhan(), schedule.getMaLichTiem());

        if (isAlreadyRegistered) {
            throw new AppException(ErrorCode.ALREADY_REGISTERED);
        }

        // 4. Kiểm tra số lượng: Lịch còn chỗ không?
        long registeredCount = lichTiemChungRepository.countRegisteredPatients(schedule.getMaLichTiem());
        if (registeredCount >= schedule.getSoLuongNguoiTiem()) {
            throw new AppException(ErrorCode.SCHEDULE_FULL);
        }

        // 5. LẤY LÔ VẮC-XIN TRỰC TIẾP TỪ LỊCH TIÊM
        // (Vì Admin đã gán lô cho lịch rồi, không cần tìm lô khác nữa)
        LoVacXin batch = schedule.getLoVacXin();
        if (batch == null || batch.getSoLuong() <= 0) {
            throw new AppException(ErrorCode.VACCINE_OUT_OF_STOCK);
        }

        // Bước 5.1: Cập nhật số lượng trong kho (Nếu muốn trừ kho ngay khi đăng ký)
        batch.setSoLuong(batch.getSoLuong() - 1);
        loVacXinRepository.save(batch);

        // 6. Tạo bản ghi đăng ký
        ChiTietDangKyTiem registration = ChiTietDangKyTiem.builder()
                .benhNhan(patient)
                .loVacXin(batch)
                .lichTiemChung(schedule)
                .thoiGianCanTiem(schedule.getNgayTiem())
                .ghiChu(request.getGhiChu())
                .build();

        chiTietDangKyTiemRepository.save(registration);

        log.info("Đăng ký thành công: Bệnh nhân {} đăng ký lịch ngày {} tại {}",
                patient.getTenBenhNhan(), schedule.getNgayTiem(), schedule.getDiaDiem());
    }

}
