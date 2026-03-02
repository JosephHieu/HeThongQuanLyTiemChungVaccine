package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.identity.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.medical.entity.HoSoBenhAn;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.impl.MedicalRecordServiceImpl;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    @Mock private BenhNhanRepository benhNhanRepository;
    @Mock private HoSoBenhAnRepository hoSoBenhAnRepository;
    @Mock private ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    @Mock private NhanVienRepository nhanVienRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    private ChiTietDangKyTiem mockRegistration;
    private HoaDon mockHoaDon;

    @BeforeEach
    void setUp() {
        // Giả lập Security Context
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("staff_test");

        mockHoaDon = HoaDon.builder()
                .maHoaDon(UUID.randomUUID())
                .trangThai(1) // Mặc định là đã thanh toán
                .build();

        mockRegistration = ChiTietDangKyTiem.builder()
                .maChiTietDKTiem(UUID.randomUUID())
                .trangThai(ChiTietDangKyTiem.STATUS_REGISTERED)
                .hoaDon(mockHoaDon)
                .benhNhan(BenhNhan.builder().tenBenhNhan("Nguyễn Văn A").build())
                .loVacXin(LoVacXin.builder().vacXin(VacXin.builder().tenVacXin("Astra").build()).build())
                .build();
    }

    @Test
    @DisplayName("Confirm Injection: Thất bại nếu hóa đơn chưa được thanh toán")
    void confirmInjection_Fail_InvoiceNotPaid() {
        // GIVEN: Hóa đơn ở trạng thái 0 (Chờ thanh toán)
        mockHoaDon.setTrangThai(0);
        when(nhanVienRepository.findByTaiKhoan_TenDangNhap(any())).thenReturn(Optional.of(new NhanVien()));
        when(chiTietDangKyTiemRepository.findById(any())).thenReturn(Optional.of(mockRegistration));

        // WHEN & THEN
        AppException ex = assertThrows(AppException.class, () ->
                medicalRecordService.confirmInjection(UUID.randomUUID(), "Bình thường", null)
        );
        assertEquals(ErrorCode.INVOICE_NOT_PAID, ex.getErrorCode());
    }

    @Test
    @DisplayName("Confirm Injection: Thất bại nếu mũi tiêm đã hoàn thành trước đó")
    void confirmInjection_Fail_AlreadyCompleted() {
        // GIVEN
        mockRegistration.setTrangThai(ChiTietDangKyTiem.STATUS_COMPLETED);
        when(nhanVienRepository.findByTaiKhoan_TenDangNhap(any())).thenReturn(Optional.of(new NhanVien()));
        when(chiTietDangKyTiemRepository.findById(any())).thenReturn(Optional.of(mockRegistration));

        // WHEN & THEN
        AppException ex = assertThrows(AppException.class, () ->
                medicalRecordService.confirmInjection(UUID.randomUUID(), "Bình thường", null)
        );
        assertEquals(ErrorCode.VACCINATION_ALREADY_COMPLETED, ex.getErrorCode());
    }

    @Test
    @DisplayName("Confirm Injection: Thành công và ghi nhận hồ sơ bệnh án")
    void confirmInjection_Success() {
        // GIVEN
        NhanVien mockStaff = NhanVien.builder().tenNhanVien("Y tá Hoa").build();
        when(nhanVienRepository.findByTaiKhoan_TenDangNhap("staff_test")).thenReturn(Optional.of(mockStaff));
        when(chiTietDangKyTiemRepository.findById(any())).thenReturn(Optional.of(mockRegistration));

        // WHEN
        medicalRecordService.confirmInjection(mockRegistration.getMaChiTietDKTiem(), "Sốt nhẹ", "Sử dụng hạ sốt");

        // THEN
        // 1. Kiểm tra trạng thái đăng ký chuyển sang COMPLETED
        assertEquals(ChiTietDangKyTiem.STATUS_COMPLETED, mockRegistration.getTrangThai());
        verify(chiTietDangKyTiemRepository).save(mockRegistration);

        // 2. Kiểm tra hồ sơ bệnh án được tạo mới
        verify(hoSoBenhAnRepository).save(argThat(record ->
                record.getPhanUngSauTiem().equals("Sốt nhẹ") &&
                        record.getNhanVienThucHien().getTenNhanVien().equals("Y tá Hoa")
        ));
    }
}