package com.josephhieu.vaccinebackend.modules.medical.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.identity.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.PendingRegistrationDTO;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.PrescribeRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdatePatientRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.UpdateProfileRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.MedicalRecordResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.PatientProfileResponse;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.VaccinationHistoryResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.HoSoBenhAn;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.MedicalRecordService;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final NhanVienRepository nhanVienRepository;

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
        // 1. Lấy thông tin nhân viên từ Token
        String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        NhanVien staff = nhanVienRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Kiểm tra bản ghi đăng ký
        ChiTietDangKyTiem registration = chiTietDangKyTiemRepository.findById(maDangKy)
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_FOUND));

        // 3. Kiểm tra trạng thái (Tránh tiêm 2 lần cho 1 mã đăng ký)
        if (ChiTietDangKyTiem.STATUS_COMPLETED.equals(registration.getTrangThai())) {
            throw new AppException(ErrorCode.VACCINATION_ALREADY_COMPLETED);
        }

        // --- CHỐT CHẶN TÀI CHÍNH MỚI ---
        HoaDon hd = registration.getHoaDon();
        if (hd == null) {
            throw new AppException(ErrorCode.INVOICE_NOT_FOUND); // chưa có hóa đơn
        }

        // Chỉ cho phép tiêm nếu hóa đơn đã được thanh toán (TrangThai = 1)
        if (hd.getTrangThai() != 1) {
            throw new AppException(ErrorCode.INVOICE_NOT_PAID); // Lỗi: Vui lòng thanh toán trước khi tiêm!
        }


        if (!ChiTietDangKyTiem.STATUS_REGISTERED.equals(registration.getTrangThai())) {
            throw new AppException(ErrorCode.INVALID_REGISTRATION_STATUS);
        }

        // 4. Cập nhật trạng thái Đăng ký sang COMPLETED
        registration.setTrangThai(ChiTietDangKyTiem.STATUS_COMPLETED);
        chiTietDangKyTiemRepository.save(registration);

        // 5. Tạo hồ sơ bệnh án
        String tacDung = (customTacDung != null && !customTacDung.isEmpty())
                ? customTacDung
                : "Theo phác đồ " + registration.getLoVacXin().getVacXin().getTenVacXin();

        HoSoBenhAn record = HoSoBenhAn.builder()
                .chiTietDangKyTiem(registration)
                .nhanVienThucHien(staff) // Đã có người tiêm
                .thoiGianTiem(LocalDateTime.now())
                .phanUngSauTiem(phanUngSauTiem != null ? phanUngSauTiem : "Bình thường")
                .thoiGianTacDung(tacDung)
                .hoaDon(registration.getHoaDon())
                .build();

        hoSoBenhAnRepository.save(record);

        log.info("Nhân viên {} xác nhận tiêm hoàn tất cho Bệnh nhân {}",
                staff.getTenNhanVien(), registration.getBenhNhan().getTenBenhNhan());
    }

    /**
     * Truy xuất hồ sơ cá nhân của người dùng đang đăng nhập.
     * Danh tính được xác thực dựa trên Token (Username) trong SecurityContext.
     * * @return PatientProfileResponse chứa thông tin hành chính của bệnh nhân.
     * @throws AppException nếu không tìm thấy thông tin người dùng.
     */
    @Override
    @Transactional(readOnly = true)
    public PatientProfileResponse getMyProfile() {

        // 1. Xác định danh tính người dùng từ Security Context (Token)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Truy vấn thông tin bệnh nhân dựa trên tài khoản đăng nhập
        BenhNhan bn = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 3. Chuyển đổi sang DTO để trả về Frontend
        return PatientProfileResponse.builder()
                .maBenhNhan(bn.getMaBenhNhan())
                .hoTen(bn.getTenBenhNhan())
                .ngaySinh(bn.getNgaySinh())
                .gioiTinh(bn.getGioiTinh())
                .diaChi(bn.getDiaChi())
                .soDienThoai(bn.getSdt())
                .nguoiGiamHo(bn.getNguoiGiamHo())
                .tenDangNhap(username)
                .build();
    }

    /**
     * Cập nhật thông tin cá nhân dành cho phân hệ Bệnh nhân (Self-service).
     * Chỉ cho phép cập nhật các trường thông tin hành chính cơ bản.
     * * @param request DTO chứa các thông tin thay đổi từ Frontend.
     * @return PatientProfileResponse Thông tin hồ sơ sau khi cập nhật thành công.
     */
    @Override
    @Transactional
    public PatientProfileResponse updateMyProfile(UpdateProfileRequest request) {

        String  username = SecurityContextHolder.getContext().getAuthentication().getName();

        BenhNhan bn = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        bn.setTenBenhNhan(request.getHoTen());
        bn.setNgaySinh(request.getNgaySinh());
        bn.setGioiTinh(request.getGioiTinh());
        bn.setDiaChi(request.getDiaChi());
        bn.setSdt(request.getSoDienThoai());
        bn.setNguoiGiamHo(request.getNguoiGiamHo());

        benhNhanRepository.save(bn);
        log.info("Bệnh nhân {} đã tự cập nhật hồ sơ cá nhân", username);

        return getMyProfile();
    }

    /**
     * Truy xuất toàn bộ lịch sử tiêm chủng của chính bệnh nhân đang đăng nhập.
     * Dữ liệu được lấy từ bảng HoSoBenhAn và định dạng lại để hiển thị lên bảng (Table).
     * * @return List<VaccinationHistoryResponse> danh sách các mũi tiêm đã hoàn thành.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VaccinationHistoryResponse> getMyVaccinationHistory() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        BenhNhan bn = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy toàn bộ lịch sử tiêm chủng từ Database
        List<HoSoBenhAn> history = hoSoBenhAnRepository.findHistoryByPatient(bn.getMaBenhNhan());

        // Chuyển đổi danh sách Entity sang danh sách DTO phẳng hóa
        return history.stream()
                .map(item -> VaccinationHistoryResponse.builder()
                        .thoiGian(item.getThoiGianTiem().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .diaDiem(item.getChiTietDangKyTiem().getLichTiemChung() != null
                        ? item.getChiTietDangKyTiem().getLichTiemChung().getDiaDiem()
                                : "Trung tâm tiêm chủng dịch vụ")
                        .tenVacXin(item.getChiTietDangKyTiem().getLoVacXin().getVacXin().getTenVacXin())
                        .loaiVacXin(item.getChiTietDangKyTiem().getLoVacXin().getVacXin().getPhongNguaBenh())
                        .lieuLuong("01 liều")
                        .nguoiTiem(item.getNhanVienThucHien() != null
                        ? item.getNhanVienThucHien().getTenNhanVien()
                                : "Nhân viên y tế")
                        .ketQua(item.getPhanUngSauTiem())
                        .build())
                .toList();
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
