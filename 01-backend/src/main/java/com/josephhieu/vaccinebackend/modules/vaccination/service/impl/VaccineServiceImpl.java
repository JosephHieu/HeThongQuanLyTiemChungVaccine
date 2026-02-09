package com.josephhieu.vaccinebackend.modules.vaccination.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccineSearchRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.RegistrationHistoryResponse;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccineServiceImpl implements VaccineService {

    private final VacXinRepository vacXinRepository;
    private final ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    private final LichTiemChungRepository lichTiemChungRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final LoVacXinRepository loVacXinRepository;
    private final HoaDonRepository hoaDonRepository;

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

        LichTiemChung schedule = null;
        LoVacXin batch = null;
        java.time.LocalDate ngayHenTiem = request.getThoiGianCanTiem();

        // 2. Xử lý phân luồng dữ liệu
        if (request.getMaLichTiemChung() != null) {
            // TRƯỜNG HỢP A: Đăng ký theo lịch trung tâm
            schedule = lichTiemChungRepository.findById(request.getMaLichTiemChung())
                    .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

            batch = schedule.getLoVacXin();
            ngayHenTiem = schedule.getNgayTiem(); // Lấy ngày từ lịch

            // Kiểm tra số lượng người đăng ký của lịch đó
            long registeredCount = lichTiemChungRepository.countRegisteredPatients(schedule.getMaLichTiem());
            if (registeredCount >= schedule.getSoLuongNguoiTiem()) {
                throw new AppException(ErrorCode.SCHEDULE_FULL);
            }
        } else {
            // TRƯỜNG HỢP B: Đăng ký lẻ từ Tra cứu vắc-xin
            if (request.getMaLoVacXin() == null) {
                throw new AppException(ErrorCode.INVALID_INFO); // Thiếu mã lô
            }
            batch = loVacXinRepository.findById(request.getMaLoVacXin())
                    .orElseThrow(() -> new AppException(ErrorCode.BATCH_NOT_FOUND));

            // ngayHenTiem đã lấy từ request.getThoiGianCanTiem() ở trên
        }

        // 3. Kiểm tra chung cho cả 2 trường hợp
        if (batch == null || batch.getSoLuong() <= 0) {
            throw new AppException(ErrorCode.VACCINE_OUT_OF_STOCK);
        }

        // 4. Kiểm tra trùng lặp (Tùy chỉnh logic kiểm tra của bạn)
        // Ví dụ: Không cho phép đăng ký cùng 1 lô vắc-xin mà chưa tiêm xong
        boolean isAlreadyRegistered = chiTietDangKyTiemRepository
                .existsByBenhNhan_MaBenhNhanAndLoVacXin_MaLoAndTrangThai(
                        patient.getMaBenhNhan(),
                        batch.getMaLo(),
                        "REGISTERED"
                );

        if (isAlreadyRegistered) {
            throw new AppException(ErrorCode.ALREADY_REGISTERED);
        }

        // 5. Trừ kho và Lưu bản ghi
        batch.setSoLuong(batch.getSoLuong() - 1);
        loVacXinRepository.save(batch);

        // 6. Lấy đơn giá bán lẻ từ thực thể Vacin liên kết với lô
        BigDecimal totalAmount = batch.getVacXin().getDonGia();
        HoaDon bill = HoaDon.builder()
                .tongTien(totalAmount)
                .ngayTao(LocalDateTime.now())
                .trangThai(0)
                .loaiHoaDon("XUAT") // Hóa đơn xuất cho bệnh nhân
                .phuongThucThanhToan("Chưa xác định")
                .build();

        HoaDon savedBill = hoaDonRepository.save(bill);

        // 7. CẬP NHẬT: Gắn hóa đơn vào bản ghi đăng ký
        ChiTietDangKyTiem registration = ChiTietDangKyTiem.builder()
                .benhNhan(patient)
                .loVacXin(batch)
                .hoaDon(savedBill)
                .lichTiemChung(schedule) // Có thể null nếu đăng ký lẻ
                .thoiGianCanTiem(ngayHenTiem)
                .ghiChu(request.getGhiChu())
                .trangThai("REGISTERED")
                .build();

        chiTietDangKyTiemRepository.save(registration);

        log.info("Đăng ký thành công: Bệnh nhân {} - Vắc xin: {} - Ngày hẹn: {}",
                patient.getTenBenhNhan(), batch.getVacXin().getTenVacXin(), ngayHenTiem);
    }

    @Override
    public List<RegistrationHistoryResponse> getMyRegistrations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BenhNhan patient = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // LƯU Ý: Nên dùng phương thức sắp xếp theo ThoiGianCanTiemDesc
        // vì không phải bản ghi nào cũng có LichTiemChung
        return chiTietDangKyTiemRepository.findByBenhNhan_MaBenhNhanOrderByThoiGianCanTiemDesc(patient.getMaBenhNhan())
                .stream()
                .map(item -> {
                    // Mặc định lấy ngày từ trường thoiGianCanTiem (luôn có dữ liệu)
                    String ngayTiem = item.getThoiGianCanTiem() != null ? item.getThoiGianCanTiem().toString() : "N/A";
                    String thoiGian = "Trong giờ hành chính"; // Mặc định cho đăng ký lẻ
                    String diaDiem = "Trung tâm tiêm chủng";

                    // Nếu có lịch trung tâm thì ghi đè thông tin chi tiết của lịch đó
                    if (item.getLichTiemChung() != null) {
                        ngayTiem = item.getLichTiemChung().getNgayTiem().toString();
                        thoiGian = item.getLichTiemChung().getThoiGianChung();
                        diaDiem = item.getLichTiemChung().getDiaDiem();
                    }

                    return RegistrationHistoryResponse.builder()
                            .maDangKy(item.getMaChiTietDKTiem())
                            .tenVacXin(item.getLoVacXin() != null ? item.getLoVacXin().getVacXin().getTenVacXin() : "N/A")
                            .soLo(item.getLoVacXin() != null ? item.getLoVacXin().getSoLo() : "N/A")
                            .ngayTiem(ngayTiem)
                            .thoiGian(thoiGian)
                            .diaDiem(diaDiem)
                            .trangThai(item.getTrangThai())
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional
    public void cancelRegistration(UUID maDangKy) {

        // 1. Tìm bản ghi đăng ký
        ChiTietDangKyTiem registration = chiTietDangKyTiemRepository.findById(maDangKy)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_FOUND));

        // 2. Bảo mật: Kiểm tra xem người hủy có đúng là người đã đăng ký không
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!registration.getBenhNhan().getTaiKhoan().getTenDangNhap().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 3. Chỉ cho phép hủy nếu trạng thái đang là REGISTERED
        if (!"REGISTERED".equals(registration.getTrangThai())) {
            throw new AppException(ErrorCode.CANNOT_CANCEL);
        }

        // 4. Cập nhật trạng thái
        registration.setTrangThai("CANCELED");
        chiTietDangKyTiemRepository.save(registration);

        // 4.5 CẬP NHẬT: Xử lý hóa đơn đi kèm
        if (registration.getHoaDon() != null) {
            HoaDon bill = registration.getHoaDon();
            // Chỉ hủy nếu hóa đơn chưa được thanh toán (trangThai == 0)
            if (bill.getTrangThai() == 0) {
                bill.setTrangThai(2); // 2: Đã hủy
                hoaDonRepository.save(bill);
            }
        }

        // 5. Hoàn lại vắc-xin vào kho
        LoVacXin batch = registration.getLoVacXin();
        batch.setSoLuong(batch.getSoLuong() + 1);
        loVacXinRepository.save(batch);

        log.info("Bệnh nhân {} đã hủy đăng ký tiêm chủng mã {}", username, maDangKy);

    }

}
