package com.josephhieu.vaccinebackend.modules.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.LoginRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.entity.PasswordResetToken;
import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.repository.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock private TaiKhoanRepository taiKhoanRepository;
    @Mock private PhanQuyenRepository phanQuyenRepository;
    @Mock private ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;
    @Mock private BenhNhanRepository benhNhanRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private JavaMailSender mailSender;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private TaiKhoan mockUser;
    private String testEmail = "joseph@gmail.com";

    @BeforeEach
    void setUp() {
        mockUser = TaiKhoan.builder()
                .maTaiKhoan(UUID.randomUUID())
                .tenDangNhap("josephhieu")
                .matKhau("hashed_password")
                .hoTen("Joseph Hieu")
                .email(testEmail)
                .trangThai(true)
                .build();
    }

    // --- TEST REGISTER ---
    @Test
    @DisplayName("Đăng ký: Thất bại khi trùng tên đăng nhập")
    void register_Fail_UserExisted() {
        RegisterRequest request = new RegisterRequest();
        request.setTenDangNhap("josephhieu");

        when(taiKhoanRepository.findByTenDangNhap(anyString())).thenReturn(Optional.of(mockUser));

        AppException ex = assertThrows(AppException.class, () -> authService.register(request));
        assertEquals(ErrorCode.USER_EXISTED, ex.getErrorCode());
    }

    // --- TEST LOGIN ---
    @Test
    @DisplayName("Đăng nhập: Thành công khi đúng thông tin")
    void login_Success() {
        // 1. Chuẩn bị dữ liệu
        LoginRequest request = new LoginRequest("josephhieu", "password123");

        // 2. Giả lập hành vi (Stubbing)
        when(taiKhoanRepository.findByTenDangNhap(anyString())).thenReturn(Optional.of(mockUser));

        // Giả lập authenticate thành công (không làm gì cả/không ném lỗi)
        when(authenticationManager.authenticate(any())).thenReturn(null);

        when(tokenProvider.generateToken(any())).thenReturn("mocked_jwt_token");

        // 3. Thực thi
        UserResponse response = authService.login(request);

        // 4. Kiểm chứng
        assertNotNull(response.getToken());
        assertEquals("josephhieu", response.getTenDangNhap());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    // --- TEST FORGOT PASSWORD ---
    @Test
    @DisplayName("Quên mật khẩu: Thành công và gửi email")
    void forgotPassword_Success() {
        when(taiKhoanRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        assertDoesNotThrow(() -> authService.processForgotPassword(testEmail));

        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    // --- TEST RESET PASSWORD ---
    @Test
    @DisplayName("Đặt lại mật khẩu: Thất bại khi Token hết hạn")
    void resetPassword_Fail_TokenExpired() {
        String token = "expired_token";
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().minusMinutes(1)) // Đã hết hạn
                .build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        AppException ex = assertThrows(AppException.class, () -> authService.resetPassword(token, "newPass"));
        assertEquals(ErrorCode.TOKEN_EXPIRED, ex.getErrorCode());
    }

    @Test
    @DisplayName("Đặt lại mật khẩu: Thành công")
    void resetPassword_Success() {
        String token = "valid_token";
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .taiKhoan(mockUser)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(taiKhoanRepository.findById(any())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(anyString())).thenReturn("new_hashed_password");

        assertDoesNotThrow(() -> authService.resetPassword(token, "newPass123"));

        verify(taiKhoanRepository, times(1)).save(mockUser);
        verify(tokenRepository, times(1)).delete(resetToken);
    }
}