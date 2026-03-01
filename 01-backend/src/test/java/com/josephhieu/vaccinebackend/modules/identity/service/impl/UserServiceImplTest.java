package com.josephhieu.vaccinebackend.modules.identity.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock private TaiKhoanRepository taiKhoanRepository;
    @Mock private PhanQuyenRepository phanQuyenRepository;
    @Mock private ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;
    @Mock private NhanVienRepository nhanVienRepository;
    @Mock private BenhNhanRepository benhNhanRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreationRequest request;
    private PhanQuyen mockRole;
    private TaiKhoan mockAccount;

    @BeforeEach
    void setUp() {
        request = UserCreationRequest.builder()
                .tenDangNhap("testuser")
                .matKhau("123456")
                .maQuyen(UUID.randomUUID().toString())
                .hoTen("Test User")
                .cmnd("123456789")
                .noiO("Hanoi")
                .email("test@gmail.com")
                .ngaySinh(LocalDate.of(1990, 1, 1))
                .build();

        mockRole = PhanQuyen.builder()
                .maQuyen(UUID.fromString(request.getMaQuyen()))
                .tenQuyen("Normal User Account")
                .build();

        mockAccount = TaiKhoan.builder()
                .maTaiKhoan(UUID.randomUUID())
                .tenDangNhap(request.getTenDangNhap())
                .build();
    }

    @Test
    @DisplayName("createNewUser: Thất bại khi tên đăng nhập đã tồn tại")
    void createNewUser_Fail_UserExisted() {
        when(taiKhoanRepository.existsByTenDangNhap(anyString())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> userService.createNewUser(request));
        assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());
    }

    @Test
    @DisplayName("createNewUser: Thành công tạo Bệnh nhân (Normal User Account)")
    void createNewUser_Success_Patient() {
        // GIVEN
        when(taiKhoanRepository.existsByTenDangNhap(anyString())).thenReturn(false);
        when(phanQuyenRepository.findById(any())).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(taiKhoanRepository.save(any())).thenReturn(mockAccount);

        // WHEN
        UserResponse response = userService.createNewUser(request);

        // THEN
        assertNotNull(response);
        verify(benhNhanRepository, times(1)).save(any()); // Phải gọi lưu Bệnh nhân
        verify(nhanVienRepository, never()).save(any());  // Không được gọi lưu Nhân viên
    }

    @Test
    @DisplayName("createNewUser: Thành công tạo Nhân viên (Administrator)")
    void createNewUser_Success_Staff() {
        // GIVEN
        mockRole.setTenQuyen("Administrator");
        when(taiKhoanRepository.existsByTenDangNhap(anyString())).thenReturn(false);
        when(phanQuyenRepository.findById(any())).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(taiKhoanRepository.save(any())).thenReturn(mockAccount);

        // WHEN
        UserResponse response = userService.createNewUser(request);

        // THEN
        verify(nhanVienRepository, times(1)).save(any()); // Phải gọi lưu Nhân viên
        verify(benhNhanRepository, never()).save(any());  // Không được gọi lưu Bệnh nhân
    }

    @Test
    @DisplayName("toggleLock: Cập nhật trạng thái tài khoản")
    void toggleLock_Success() {
        // GIVEN
        mockAccount.setTrangThai(true);
        when(taiKhoanRepository.findById(any())).thenReturn(Optional.of(mockAccount));

        // WHEN
        userService.toggleLock(mockAccount.getMaTaiKhoan());

        // THEN
        assertFalse(mockAccount.isTrangThai());
        verify(taiKhoanRepository, times(1)).save(mockAccount);
    }
}