package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.VaccinationRegistrationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.service.impl.VaccineServiceImpl;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VaccineServiceTest {

    @Mock private BenhNhanRepository benhNhanRepository;
    @Mock private LoVacXinRepository loVacXinRepository;
    @Mock private ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    @Mock private HoaDonRepository hoaDonRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private VaccineServiceImpl vaccineService;

    private BenhNhan mockPatient;
    private LoVacXin mockBatch;
    private final String CURRENT_USER = "hieu_patient";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(CURRENT_USER);

        mockPatient = BenhNhan.builder()
                .maBenhNhan(UUID.randomUUID())
                .tenBenhNhan("Joseph Hieu")
                .taiKhoan(TaiKhoan.builder().tenDangNhap(CURRENT_USER).build())
                .build();

        mockBatch = LoVacXin.builder()
                .maLo(UUID.randomUUID())
                .soLuong(10)
                .vacXin(VacXin.builder().donGia(new BigDecimal("200000")).build())
                .build();
    }

    @Test
    @DisplayName("Register: Thành công - Giảm tồn kho và tạo hóa đơn chờ")
    void registerVaccination_Success() {
        // GIVEN
        VaccinationRegistrationRequest request = VaccinationRegistrationRequest.builder()
                .maLoVacXin(mockBatch.getMaLo())
                .build();

        when(benhNhanRepository.findByTaiKhoan_TenDangNhap(CURRENT_USER)).thenReturn(Optional.of(mockPatient));
        when(loVacXinRepository.findByIdWithLock(any())).thenReturn(Optional.of(mockBatch));
        when(hoaDonRepository.save(any(HoaDon.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN
        vaccineService.registerVaccination(request);

        // THEN
        assertEquals(9, mockBatch.getSoLuong()); // 10 - 1 = 9
        verify(loVacXinRepository).save(mockBatch);
        verify(hoaDonRepository).save(argThat(hd ->
                hd.getTrangThai() == 0 && hd.getTongTien().compareTo(new BigDecimal("200000")) == 0
        ));
        verify(chiTietDangKyTiemRepository).save(any());
    }

    @Test
    @DisplayName("Cancel: Thành công - Hoàn tồn kho và hủy hóa đơn chưa thanh toán")
    void cancelRegistration_Success() {
        // GIVEN
        HoaDon pendingInvoice = HoaDon.builder().trangThai(0).build();
        ChiTietDangKyTiem registration = ChiTietDangKyTiem.builder()
                .maChiTietDKTiem(UUID.randomUUID())
                .benhNhan(mockPatient)
                .loVacXin(mockBatch)
                .hoaDon(pendingInvoice)
                .trangThai("REGISTERED")
                .build();

        when(chiTietDangKyTiemRepository.findById(any())).thenReturn(Optional.of(registration));

        // WHEN
        vaccineService.cancelRegistration(registration.getMaChiTietDKTiem());

        // THEN
        assertEquals(11, mockBatch.getSoLuong()); // 10 + 1 = 11
        assertEquals(2, pendingInvoice.getTrangThai()); // Hóa đơn chuyển sang trạng thái 2 (Hủy)
        assertEquals("CANCELED", registration.getTrangThai());
        verify(loVacXinRepository).save(mockBatch);
        verify(hoaDonRepository).save(pendingInvoice);
    }

    @Test
    @DisplayName("Cancel: Lỗi khi bệnh nhân này hủy lịch của bệnh nhân khác")
    void cancelRegistration_Unauthorized() {
        // GIVEN: Lịch này của một người dùng khác
        mockPatient.getTaiKhoan().setTenDangNhap("hacker_user");
        ChiTietDangKyTiem registration = ChiTietDangKyTiem.builder()
                .benhNhan(mockPatient)
                .build();
        when(chiTietDangKyTiemRepository.findById(any())).thenReturn(Optional.of(registration));

        // WHEN & THEN
        AppException ex = assertThrows(AppException.class, () ->
                vaccineService.cancelRegistration(UUID.randomUUID())
        );
        assertEquals(ErrorCode.UNAUTHORIZED, ex.getErrorCode());
    }
}