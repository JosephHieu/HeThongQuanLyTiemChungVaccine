package com.josephhieu.vaccinebackend.modules.medical.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.PendingRegistrationDTO;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.PrescribeRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdatePatientRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.MedicalRecordResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.HoSoBenhAn;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.MedicalRecordService;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final BenhNhanRepository benhNhanRepository;
    private final HoSoBenhAnRepository hoSoBenhAnRepository;
    private final ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    private final LoVacXinRepository loVacXinRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecord(UUID maBenhNhan) {
        BenhNhan bn = benhNhanRepository.findById(maBenhNhan)
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        List<HoSoBenhAn> history = hoSoBenhAnRepository.findHistoryByPatient(maBenhNhan);
        HoSoBenhAn latest = history.isEmpty() ? null : history.get(0);

        // Lấy TOÀN BỘ danh sách đang chờ thay vì chỉ lấy 1 cái đầu tiên
        List<ChiTietDangKyTiem> pending = chiTietDangKyTiemRepository.findPendingAppointments(maBenhNhan);

        return mapToResponse(bn, latest, pending);
    }

    @Override
    @Transactional
    public MedicalRecordResponse updatePatientInfo(UUID maBenhNhan, UpdatePatientRequest request) {
        BenhNhan bn = benhNhanRepository.findById(maBenhNhan)
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        // Cập nhật dữ liệu từ Request
        bn.setTenBenhNhan(request.getTenBenhNhan());
        bn.setNgaySinh(request.getNgaySinh());
        bn.setGioiTinh(request.getGioiTinh());
        bn.setDiaChi(request.getDiaChi());
        bn.setSdt(request.getSdt());
        bn.setNguoiGiamHo(request.getNguoiGiamHo());

        benhNhanRepository.save(bn);
        return getMedicalRecord(maBenhNhan); // Trả về hồ sơ đầy đủ sau khi update
    }

    @Override
    @Transactional
    public void prescribeVaccine(UUID maBenhNhan, PrescribeRequest request) {
        BenhNhan bn = benhNhanRepository.findById(maBenhNhan)
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        LoVacXin lo = loVacXinRepository.findById(request.getMaLoVacXin())
                .orElseThrow(() -> new AppException(ErrorCode.BATCH_NOT_FOUND));

        // Tạo mới một ChiTietDangKyTiem để làm lịch hẹn (Kê đơn)
        ChiTietDangKyTiem prescription = ChiTietDangKyTiem.builder()
                .benhNhan(bn)
                .loVacXin(lo)
                .thoiGianCanTiem(request.getThoiGianCanTiem())
                .ghiChu(request.getGhiChu())
                .lichTiemChung(null)
                .build();

        chiTietDangKyTiemRepository.save(prescription);
    }

    @Override
    @Transactional
    public void confirmInjection(UUID maDangKy, String phanUngSauTiem, String customTacDung) {
        ChiTietDangKyTiem registration = chiTietDangKyTiemRepository.findById(maDangKy)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_FOUND));

        // Kiểm tra nếu không phải trạng thái REGISTERED thì không cho tiêm
        if (!ChiTietDangKyTiem.STATUS_REGISTERED.equals(registration.getTrangThai())) {
            if (ChiTietDangKyTiem.STATUS_COMPLETED.equals(registration.getTrangThai())) {
                throw new AppException(ErrorCode.VACCINATION_ALREADY_COMPLETED);
            }
            throw new AppException(ErrorCode.INVALID_REGISTRATION_STATUS);
        }

        LoVacXin lo = registration.getLoVacXin();
        if (lo == null || lo.getSoLuong() <= 0) {
            throw new AppException(ErrorCode.OUT_OF_STOCK); // Bây giờ đã có trong Enum
        }
        lo.setSoLuong(lo.getSoLuong() - 1);
        loVacXinRepository.save(lo);

        // 4. Cập nhật trạng thái Đăng ký sang COMPLETED
        registration.setTrangThai(ChiTietDangKyTiem.STATUS_COMPLETED);
        chiTietDangKyTiemRepository.save(registration);

        // 5. Tạo hồ sơ bệnh án (HoSoBenhAn)
        // Tùy chỉnh thoiGianTacDung: Nếu không truyền từ UI, lấy mặc định từ tên Vacxin
        String tacDung = (customTacDung != null && !customTacDung.isEmpty())
                ? customTacDung
                : "Theo phác đồ " + registration.getLoVacXin().getVacXin().getTenVacXin();

        HoSoBenhAn record = HoSoBenhAn.builder()
                .chiTietDangKyTiem(registration)
                .thoiGianTiem(LocalDateTime.now())
                .phanUngSauTiem(phanUngSauTiem != null ? phanUngSauTiem : "Bình thường")
                .thoiGianTacDung(tacDung)
                .hoaDon(lo.getHoaDon()) // Thừa hưởng hóa đơn từ lô vắc-xin nếu có
                .build();

        hoSoBenhAnRepository.save(record);

        log.info("Bệnh nhân {} đã tiêm xong mũi {}. Kho còn lại: {}",
                registration.getBenhNhan().getTenBenhNhan(),
                lo.getVacXin().getTenVacXin(),
                lo.getSoLuong());
    }
    /**
     * Helper Method: Chuyển đổi các Entity rời rạc thành DTO tổng hợp.
     */
    private MedicalRecordResponse mapToResponse(BenhNhan bn, HoSoBenhAn latest, List<ChiTietDangKyTiem> pending) {
        int tuoi = (bn.getNgaySinh() != null) ? Period.between(bn.getNgaySinh(), LocalDate.now()).getYears() : 0;

        // Mũi tiêm tiếp theo (lấy cái gần nhất trong danh sách pending)
        ChiTietDangKyTiem next = pending.isEmpty() ? null : pending.get(0);

        // Map danh sách Pending sang DTO cho Tab "Thực hiện tiêm"
        List<PendingRegistrationDTO> pendingDTOs = pending.stream()
                .map(p -> PendingRegistrationDTO.builder()
                        .id(p.getMaChiTietDKTiem())
                        .tenVacXin(p.getLoVacXin().getVacXin().getTenVacXin())
                        .soLo(p.getLoVacXin().getSoLo())
                        .ngayHen(p.getThoiGianCanTiem().format(DATE_FORMATTER))
                        .build())
                .toList();

        return MedicalRecordResponse.builder()
                .id(bn.getMaBenhNhan())
                .hoTen(bn.getTenBenhNhan())
                .gioiTinh(bn.getGioiTinh())
                .tuoi(tuoi)
                .dienThoai(bn.getSdt())
                .diaChi(bn.getDiaChi())
                .nguoiGiamHo(bn.getNguoiGiamHo())
                .ngaySinh(bn.getNgaySinh() != null ? bn.getNgaySinh().toString() : "")

                // Thông tin quá khứ
                .vacxinDaTiem(latest != null ? latest.getChiTietDangKyTiem().getLoVacXin().getVacXin().getTenVacXin() : "Chưa có dữ liệu")
                .maLo(latest != null ? latest.getChiTietDangKyTiem().getLoVacXin().getSoLo() : "N/A")
                .thoiGianTiemTruoc(latest != null ? latest.getThoiGianTiem().format(DATE_FORMATTER) : "N/A")
                .phanUng(latest != null ? latest.getPhanUngSauTiem() : "Bình thường")

                // Thông tin tương lai
                .vacxinCanTiem(next != null ? next.getLoVacXin().getVacXin().getTenVacXin() : "Chưa có chỉ định")
                .thoiGianTiemTiepTheo(next != null ? next.getThoiGianCanTiem().format(DATE_FORMATTER) : "Chưa đặt lịch")

                // Danh sách chờ tiêm (MỚI)
                .pendingRegistrations(pendingDTOs)
                .build();
    }
}
