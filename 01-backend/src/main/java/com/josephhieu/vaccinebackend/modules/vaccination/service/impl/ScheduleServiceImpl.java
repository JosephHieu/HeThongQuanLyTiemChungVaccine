package com.josephhieu.vaccinebackend.modules.vaccination.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.ScheduleResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietNhanVienThamGia;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.LichTiemChung;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.id.NhanVienThamGiaId;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietNhanVienThamGiaRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.LichTiemChungRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final LichTiemChungRepository lichTiemChungRepository;
    private final ChiTietNhanVienThamGiaRepository chiTietNhanVienThamGiaRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    @Transactional
    public ScheduleResponse createScheduleService(ScheduleCreationRequest request) {

        // 1. Ánh xạ từ Request DTO sang Entity LichTiemChung
        LichTiemChung lichTiemChung = LichTiemChung.builder()
                .ngayTiem(request.getNgayTiem())
                .thoiGianChung(request.getThoiGian()) // Map: thoiGian -> thoiGianChung
                .soLuongNguoiTiem(request.getSoLuong()) // Map: soLuong -> soLuongNguoiTiem
                .doiTuong(request.getDoTuoi()) // Map: doTuoi -> doiTuong
                .diaDiem(request.getDiaDiem())
                .ghiChu(request.getGhiChu())
                .build();

        // Lưu thông tin lịch tiêm chính
        lichTiemChung = lichTiemChungRepository.save(lichTiemChung);

        // 2. Xử lý gán danh sách bác sĩ tham gia đợt tiêm
        if (request.getDanhSachBacSiIds() != null && !request.getDanhSachBacSiIds().isEmpty()) {
            for (UUID maNhanVien : request.getDanhSachBacSiIds()) {

                NhanVien nhanVien = nhanVienRepository.findById(maNhanVien)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                // Tạo bản ghi trong bảng trung gian CHITIET_NV_THAMGIA
                ChiTietNhanVienThamGia chiTiet = ChiTietNhanVienThamGia.builder()
                        .id(new NhanVienThamGiaId(maNhanVien, lichTiemChung.getMaLichTiem()))
                        .nhanVien(nhanVien)
                        .lichTiemChung(lichTiemChung)
                        .build();

                chiTietNhanVienThamGiaRepository.save(chiTiet);
            }
        }

        // 3. Trả về kết quả (Sử dụng hàm mapToResponse phía dưới)
        return mapToResponse(lichTiemChung);
    }

    /**
     * Chuyển đổi Entity sang Response DTO để hiển thị trên giao diện.
     */
    private ScheduleResponse mapToResponse(LichTiemChung entity) {
        return ScheduleResponse.builder()
                .maLichTiemChung(entity.getMaLichTiem())
                .ngayTiem(entity.getNgayTiem())
                .thoiGian(entity.getThoiGianChung())
                .diaDiem(entity.getDiaDiem())
                .soLuong(entity.getSoLuongNguoiTiem())
                .build();
    }
}