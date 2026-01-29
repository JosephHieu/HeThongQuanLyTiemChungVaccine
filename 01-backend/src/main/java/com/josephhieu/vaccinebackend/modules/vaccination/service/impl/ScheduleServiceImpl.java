package com.josephhieu.vaccinebackend.modules.vaccination.service.impl;

import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.ScheduleResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.StaffSummaryResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietNhanVienThamGia;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.LichTiemChung;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.id.NhanVienThamGiaId;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietNhanVienThamGiaRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.LichTiemChungRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final LichTiemChungRepository lichTiemChungRepository;
    private final ChiTietNhanVienThamGiaRepository chiTietNhanVienThamGiaRepository;
    private final NhanVienRepository nhanVienRepository;
    private final ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;

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

        lichTiemChung = lichTiemChungRepository.save(lichTiemChung);

        // 2. Xử lý gán danh sách bác sĩ tham gia đợt tiêm
        if (request.getDanhSachBacSiIds() != null) {
            for (UUID maNhanVien : request.getDanhSachBacSiIds()) {
                NhanVien nhanVien = nhanVienRepository.findById(maNhanVien)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ScheduleResponse> getAllSchedules(int page, int size, String search, LocalDate start, LocalDate end) {

        int validatePage = (page < 1) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(validatePage, size, Sort.by("ngayTiem").descending());

        LocalDate startDate = (start != null) ? start : LocalDate.of(2000, 1, 1);
        LocalDate endDate = (end != null) ? end : LocalDate.of(2100, 12, 31);
        String searchKey = (search != null) ? search : "";

        Page<LichTiemChung> schedulePage = lichTiemChungRepository
                .findByNgayTiemBetweenAndDiaDiemContaining(startDate, endDate, searchKey, pageable);

        List<ScheduleResponse> data = schedulePage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<ScheduleResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(schedulePage.getTotalPages())
                .totalElements(schedulePage.getTotalElements())
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(UUID id, ScheduleCreationRequest request) {

        // 1. Tìm lịch tiêm hiện có
        LichTiemChung schedule = lichTiemChungRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Cập nhật các thông tin cơ bản
        schedule.setNgayTiem(request.getNgayTiem());
        schedule.setThoiGianChung(request.getThoiGian());
        schedule.setSoLuongNguoiTiem(request.getSoLuong());
        schedule.setDoiTuong(request.getDoTuoi());
        schedule.setDiaDiem(request.getDiaDiem());
        schedule.setGhiChu(request.getGhiChu());

        lichTiemChungRepository.save(schedule);

        // 3. Cập nhật danh sách bác sĩ (Xóa cũ - Thêm mới là cách an toàn nhất)
        // Bạn cần thêm phương thức deleteByLichTiemChung vào ChiTietNhanVienThamGiaRepository
        chiTietNhanVienThamGiaRepository.deleteByLichTiemChung(schedule);

        if (request.getDanhSachBacSiIds() != null) {
            for (UUID maNhanVien : request.getDanhSachBacSiIds()) {
                NhanVien nhanVien = nhanVienRepository.findById(maNhanVien)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                ChiTietNhanVienThamGia chiTiet = ChiTietNhanVienThamGia.builder()
                        .id(new NhanVienThamGiaId(maNhanVien, schedule.getMaLichTiem()))
                        .nhanVien(nhanVien)
                        .lichTiemChung(schedule)
                        .build();
                chiTietNhanVienThamGiaRepository.save(chiTiet);
            }
        }

        return mapToResponse(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(UUID id) {

        LichTiemChung schedule = lichTiemChungRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xóa liên kết bác sĩ trực trước
        chiTietNhanVienThamGiaRepository.deleteByLichTiemChung(schedule);

        // Xóa lịch tiêm
        lichTiemChungRepository.delete(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleResponse getScheduleByDate(LocalDate date) {
        // Tìm lịch tiêm theo ngày
        return lichTiemChungRepository.findByNgayTiem(date)
                .map(this::mapToResponse)
                .orElse(null); // Trả về null nếu ngày đó chưa có lịch
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getActiveDatesInPeriod(LocalDate start, LocalDate end) {
        // Lấy danh sách các ngày có lịch tiêm trong khoảng thời gian (thường là 1 tháng)
        return lichTiemChungRepository.findAllByNgayTiemBetween(start, end)
                .stream()
                .map(LichTiemChung::getNgayTiem)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RegistrationResponse> getRegistrationsBySchedule(UUID maLichTiem, int page, int size) {
        int validatePage = (page < 1) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(validatePage, size, Sort.by("thoiGianCanTiem").descending());

        // Gọi Repository đã có của bạn
        Page<ChiTietDangKyTiem> registrationPage = chiTietDangKyTiemRepository
                .findByLichTiemChung_MaLichTiem(maLichTiem, pageable);

        List<RegistrationResponse> data = registrationPage.getContent().stream()
                .map(reg -> RegistrationResponse.builder()
                        .maDangKy(reg.getMaChiTietDKTiem())
                        .tenBenhNhan(reg.getBenhNhan().getTenBenhNhan())
                        .soDienThoai(reg.getBenhNhan().getSdt())
                        // Lấy tên vắc xin từ lô vắc xin mà bệnh nhân đã chọn
                        .tenVacXin(reg.getLoVacXin() != null ?
                                reg.getLoVacXin().getVacXin().getTenVacXin() : "Chưa chọn")
                        .ngayDangKy(reg.getThoiGianCanTiem() != null ? reg.getThoiGianCanTiem().atStartOfDay() : null)
                        .build())
                .toList();

        return PageResponse.<RegistrationResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(registrationPage.getTotalPages())
                .totalElements(registrationPage.getTotalElements())
                .data(data)
                .build();
    }

    /**
     * Chuyển đổi Entity sang Response DTO để hiển thị trên giao diện.
     */
    private ScheduleResponse mapToResponse(LichTiemChung entity) {

        long registeredCount = lichTiemChungRepository.countRegisteredPatients(entity.getMaLichTiem());

        List<StaffSummaryResponse> staffList = chiTietNhanVienThamGiaRepository
                .findByLichTiemChung_MaLichTiem(entity.getMaLichTiem()).stream()
                .map(ct -> StaffSummaryResponse.builder()
                        .maNhanVien(ct.getNhanVien().getMaNhanVien())
                        // LƯU Ý: Kiểm tra trường tên trong NhanVien là 'hoTen' hay 'tenNhanVien'
                        .tenNhanVien(ct.getNhanVien().getTenNhanVien())
                        .build())
                .toList();

        return ScheduleResponse.builder()
                .maLichTiemChung(entity.getMaLichTiem())
                .ngayTiem(entity.getNgayTiem())
                .thoiGian(entity.getThoiGianChung())
                .diaDiem(entity.getDiaDiem())
                .soLuong(entity.getSoLuongNguoiTiem())
                .daDangKy((int) registeredCount)
                .danhSachBacSi(staffList)
                .ghiChu(entity.getGhiChu())
                .doTuoi(entity.getDoiTuong())
                .build();
    }
}