package com.josephhieu.vaccinebackend.modules.auth.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.LoginRequest;
import com.josephhieu.vaccinebackend.modules.auth.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.modules.auth.security.JwtTokenProvider;
import com.josephhieu.vaccinebackend.modules.auth.service.AuthService;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.entity.*;
import com.josephhieu.vaccinebackend.modules.identity.entity.id.ChiTietPhanQuyenId;
import com.josephhieu.vaccinebackend.modules.identity.repository.*;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final PhanQuyenRepository phanQuyenRepository;
    private final ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (request.getTenDangNhap() == null || request.getTenDangNhap().isBlank()) {
            throw new AppException(ErrorCode.MISSING_INFO);
        }

        if (taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        TaiKhoan user = TaiKhoan.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .cmnd(request.getCmnd())
                .noiO(request.getNoiO())
                .email(request.getEmail())
                .trangThai(true)
                .build();

        user = taiKhoanRepository.save(user);

        // Tạo hồ sơ bệnh nhân
        BenhNhan profile = BenhNhan.builder()
                .taiKhoan(user)
                .tenBenhNhan(user.getHoTen())
                .ngaySinh(request.getNgaySinh())
                .sdt(request.getSdt())
                .gioiTinh(request.getGioiTinh())
                .diaChi(user.getNoiO())
                .build();
        benhNhanRepository.save(profile);

        // Gán quyền mặc định
        PhanQuyen role = phanQuyenRepository.findByTenQuyen("Normal User Account")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        ChiTietPhanQuyen chiTiet = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(role.getMaQuyen(), user.getMaTaiKhoan()))
                .phanQuyen(role)
                .taiKhoan(user)
                .build();
        chiTietPhanQuyenRepository.save(chiTiet);

        return mapToUserResponse(user, Collections.singleton(role.getTenQuyen()), null);
    }

    @Override
    public UserResponse login(LoginRequest request) {
        TaiKhoan user = taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!passwordEncoder.matches(request.getMatKhau(), user.getMatKhau())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!user.isTrangThai()) {
            throw new AppException(ErrorCode.USER_LOCKED);
        }

        Set<String> roles = user.getChiTietPhanQuyens().stream()
                .map(ct -> ct.getPhanQuyen().getTenQuyen())
                .collect(Collectors.toSet());

        String token = tokenProvider.generateToken(user);
        return mapToUserResponse(user, roles, token);
    }

    @Override
    @Transactional
    public void processForgotPassword(String email) {
        // 1. Tìm tài khoản qua email (Cần thêm findByEmail vào Repository)
        TaiKhoan user = taiKhoanRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Xóa token cũ của user này nếu có
        tokenRepository.deleteByTaiKhoan(user);

        // 3. Tạo token reset mật khẩu mới
        String tokenValue = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(tokenValue)
                .taiKhoan(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);

        // 4. Gửi email
        sendResetEmail(user.getEmail(), user.getHoTen(), tokenValue);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_INVALID));

        if (resetToken.isExpired()) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        TaiKhoan user = taiKhoanRepository.findById(resetToken.getTaiKhoan().getMaTaiKhoan())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setMatKhau(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(user);

        // Xóa token sau khi dùng xong
        tokenRepository.delete(resetToken);
    }

    private void sendResetEmail(String toEmail, String hoTen, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[VaxTrack Pro] Yêu cầu đặt lại mật khẩu");

            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            String content = "<h3>Chào " + hoTen + ",</h3>"
                    + "<p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấn vào link dưới đây để tiếp tục:</p>"
                    + "<a href='" + resetLink + "'>ĐẶT LẠI MẬT KHẨU</a>"
                    + "<p>Link này sẽ hết hạn trong 15 phút.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi gửi mail: ", e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private UserResponse mapToUserResponse(TaiKhoan user, Set<String> roles, String token) {
        return UserResponse.builder()
                .maTaiKhoan(user.getMaTaiKhoan())
                .tenDangNhap(user.getTenDangNhap())
                .hoTen(user.getHoTen())
                .email(user.getEmail())
                .roles(roles)
                .token(token)
                .build();
    }
}