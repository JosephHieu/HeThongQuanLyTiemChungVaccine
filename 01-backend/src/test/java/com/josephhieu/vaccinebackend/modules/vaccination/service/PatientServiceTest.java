package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.identity.repository.TaiKhoanRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.UpdateProfileRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.PatientProfileResponse;
import com.josephhieu.vaccinebackend.modules.vaccination.service.impl.PatientServiceImpl;
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

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock private BenhNhanRepository benhNhanRepository;
    @Mock private TaiKhoanRepository taiKhoanRepository;
    @Mock private HoSoBenhAnRepository hoSoBenhAnRepository;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private PatientServiceImpl patientService;

    private final String MOCK_USERNAME = "joseph_hieu";
    private BenhNhan mockPatient;
    private TaiKhoan mockAccount;

    @BeforeEach
    void setUp() {
        // Giả lập SecurityContext cho toàn bộ class test
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(MOCK_USERNAME);

        mockAccount = TaiKhoan.builder()
                .tenDangNhap(MOCK_USERNAME)
                .email("hieu@example.com")
                .cmnd("123456789")
                .build();

        mockPatient = BenhNhan.builder()
                .maBenhNhan(UUID.randomUUID())
                .tenBenhNhan("Joseph Hieu")
                .taiKhoan(mockAccount)
                .sdt("0909123456")
                .build();
    }

    @Test
    @DisplayName("GetProfile: Thành công khi lấy hồ sơ cá nhân")
    void getMyProfile_Success() {
        // GIVEN
        when(benhNhanRepository.findByTaiKhoan_TenDangNhap(MOCK_USERNAME))
                .thenReturn(Optional.of(mockPatient));
        when(hoSoBenhAnRepository.findHistoryByPatient(any())).thenReturn(new ArrayList<>());

        // WHEN
        PatientProfileResponse response = patientService.getMyProfile();

        // THEN
        assertThat(response.getTenBenhNhan()).isEqualTo("Joseph Hieu");
        assertThat(response.getEmail()).isEqualTo("hieu@example.com");
        verify(hoSoBenhAnRepository).findHistoryByPatient(mockPatient.getMaBenhNhan());
    }

    @Test
    @DisplayName("GetProfile: Thất bại khi tài khoản không tồn tại")
    void getMyProfile_UserNotFound_ThrowException() {
        // GIVEN
        when(benhNhanRepository.findByTaiKhoan_TenDangNhap(MOCK_USERNAME))
                .thenReturn(Optional.empty());

        // WHEN & THEN
        AppException ex = assertThrows(AppException.class, () -> patientService.getMyProfile());
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }

    @Test
    @DisplayName("UpdateProfile: Cập nhật thành công thông tin SDT và Email")
    void updateProfile_Success() {
        // GIVEN
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .sdt("0888999000")
                .email("new_email@example.com")
                .diaChi("Hồ Chí Minh")
                .gioiTinh("Nam")
                .build();

        when(benhNhanRepository.findByTaiKhoan_TenDangNhap(MOCK_USERNAME))
                .thenReturn(Optional.of(mockPatient));

        // WHEN
        patientService.updateProfile(request);

        // THEN
        assertThat(mockPatient.getSdt()).isEqualTo("0888999000");
        assertThat(mockAccount.getEmail()).isEqualTo("new_email@example.com");

        verify(taiKhoanRepository).save(mockAccount);
        verify(benhNhanRepository).save(mockPatient);
    }
}