package com.josephhieu.vaccinebackend.modules.medical.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.PrescribeRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdatePatientRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.MedicalRecordResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.HoSoBenhAn;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.MedicalRecordService;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final BenhNhanRepository benhNhanRepository;
    private final HoSoBenhAnRepository hoSoBenhAnRepository;
    private final ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    private final LoVacXinRepository loVacXinRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecord(UUID maBenhNhan) {
        // 1. Lấy thông tin bệnh nhân
        BenhNhan bn = benhNhanRepository.findById(maBenhNhan)
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        // 2. Lấy mũi tiêm gần nhất từ lịch sử (HoSoBenhAn)
        List<HoSoBenhAn> history = hoSoBenhAnRepository.findHistoryByPatient(maBenhNhan);
        HoSoBenhAn latest = history.isEmpty() ? null : history.get(0);

        // 3. Lấy lịch hẹn tiếp theo (ChiTietDangKyTiem chưa có hồ sơ)
        // Sử dụng hàm query mà chúng ta đã bổ sung ở bước Repository
        List<ChiTietDangKyTiem> pending = chiTietDangKyTiemRepository.findPendingAppointments(maBenhNhan);
        ChiTietDangKyTiem next = pending.isEmpty() ? null : pending.get(0);

        return mapToResponse(bn, latest, next);
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
                .build();

        chiTietDangKyTiemRepository.save(prescription);
    }

    /**
     * Helper Method: Chuyển đổi các Entity rời rạc thành DTO tổng hợp.
     */
    private MedicalRecordResponse mapToResponse(BenhNhan bn, HoSoBenhAn latest, ChiTietDangKyTiem next) {
        int tuoi = (bn.getNgaySinh() != null) ? Period.between(bn.getNgaySinh(), LocalDate.now()).getYears() : 0;

        return MedicalRecordResponse.builder()
                .id(bn.getMaBenhNhan())
                .hoTen(bn.getTenBenhNhan())
                .gioiTinh(bn.getGioiTinh())
                .tuoi(tuoi)
                .dienThoai(bn.getSdt())
                .diaChi(bn.getDiaChi())
                .nguoiGiamHo(bn.getNguoiGiamHo())
                // Thông tin quá khứ
                .vacxinDaTiem(latest != null ? latest.getChiTietDangKyTiem().getLoVacXin().getVacXin().getTenVacXin() : "Chưa có dữ liệu")
                .maLo(latest != null ? latest.getChiTietDangKyTiem().getLoVacXin().getMaLo().toString() : "N/A")
                .thoiGianTiemTruoc(latest != null ? latest.getThoiGianTiem().format(DATE_FORMATTER) : "N/A")
                .phanUng(latest != null ? latest.getPhanUngSauTiem() : "Bình thường")
                // Thông tin tương lai
                .vacxinCanTiem(next != null ? next.getLoVacXin().getVacXin().getTenVacXin() : "Chưa có chỉ định")
                .thoiGianTiemTiepTheo(next != null ? next.getThoiGianCanTiem().format(DATE_FORMATTER) : "Chưa đặt lịch")
                .build();
    }
}
