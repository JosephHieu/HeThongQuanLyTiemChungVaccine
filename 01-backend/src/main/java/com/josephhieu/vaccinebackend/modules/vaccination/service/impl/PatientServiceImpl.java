package com.josephhieu.vaccinebackend.modules.vaccination.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.identity.repository.TaiKhoanRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.UpdateProfileRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.PatientProfileResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccinationHistoryResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final BenhNhanRepository benhNhanRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final HoSoBenhAnRepository hoSoBenhAnRepository;


    @Override
    public PatientProfileResponse getMyProfile() {
        // 1. Lấy tên đăng nhập từ Token (SecurityContext)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Lấy thông tin bệnh nhân kèm theo tài khoản (nhờ mối quan hệ OneToOne)
        BenhNhan patient = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 3. Lấy lịch sử tiêm chủng thực tế từ bảng HOSOBENHAN
        List<VaccinationHistoryResponse> history = hoSoBenhAnRepository.findHistoryByPatient(patient.getMaBenhNhan())
                .stream()
                .map(hs -> VaccinationHistoryResponse.builder()
                        .ngayTiem(hs.getThoiGianTiem() != null ? hs.getThoiGianTiem().toString() : "N/A")
                        .diaDiem(hs.getChiTietDangKyTiem().getLichTiemChung().getDiaDiem())
                        .tenVacXin(hs.getChiTietDangKyTiem().getLoVacXin().getVacXin().getTenVacXin())
                        .loaiVacXin(hs.getChiTietDangKyTiem().getLoVacXin().getVacXin().getLoaiVacXin().getTenLoaiVacXin())
                        .nhanVienThucHien("Hệ thống")
                        .phanUngSauTiem(hs.getPhanUngSauTiem())
                        .ghiChu("Không có ghi chú") // Thay vì gọi hs.getGhiChu()
                        .build())
                .toList();

        // 4. Map dữ liệu sang Profile Response
        return PatientProfileResponse.builder()
                .maBenhNhan(patient.getMaBenhNhan())
                .tenBenhNhan(patient.getTenBenhNhan())
                .ngaySinh(patient.getNgaySinh() != null ? patient.getNgaySinh().toString() : "N/A")
                .gioiTinh(patient.getGioiTinh())
                .sdt(patient.getSdt())
                .diaChi(patient.getDiaChi())
                .nguoiGiamHo(patient.getNguoiGiamHo())
                .email(patient.getTaiKhoan().getEmail())
                .cmnd(patient.getTaiKhoan().getCmnd())
                .lichSuTiem(history)
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BenhNhan patient = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Cập nhật thông tin bảng BENHNHAN
        patient.setSdt(request.getSdt());
        patient.setDiaChi(request.getDiaChi());
        patient.setGioiTinh(request.getGioiTinh());

        // Cập nhật thông tin bảng TAIKHOAN (Email)
        TaiKhoan account = patient.getTaiKhoan();
        if (account != null) {
            account.setEmail(request.getEmail());
            taiKhoanRepository.save(account);
        }

        benhNhanRepository.save(patient);
        log.info("Bệnh nhân {} đã cập nhật thông tin cá nhân thành công.", username);
    }


}