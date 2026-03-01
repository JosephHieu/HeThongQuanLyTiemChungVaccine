package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.PasswordResetToken;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private TaiKhoan savedAccount;

    @BeforeEach
    void setUp() {
        // Tạo tài khoản mẫu để gán Token
        TaiKhoan tk = TaiKhoan.builder()
                .tenDangNhap("reset_user")
                .email("reset@gmail.com")
                .build();
        savedAccount = entityManager.persist(tk);
        entityManager.flush();
    }

    @Test
    @DisplayName("findByToken: Tìm thấy token hợp lệ và kiểm tra ngày hết hạn")
    void findByToken_Success() {
        // GIVEN
        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken token = PasswordResetToken.builder()
                .token(rawToken)
                .taiKhoan(savedAccount)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        entityManager.persist(token);
        entityManager.flush();

        // WHEN
        Optional<PasswordResetToken> result = passwordResetTokenRepository.findByToken(rawToken);

        // THEN
        assertTrue(result.isPresent());
        assertEquals("reset_user", result.get().getTaiKhoan().getTenDangNhap());
        assertFalse(result.get().isExpired(), "Token mới tạo không được hết hạn");
    }

    @Test
    @DisplayName("deleteByTaiKhoan: Xóa bỏ token cũ khi người dùng yêu cầu lại")
    void deleteByTaiKhoan_Success() {
        // GIVEN
        PasswordResetToken token = PasswordResetToken.builder()
                .token("old-token")
                .taiKhoan(savedAccount)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        entityManager.persist(token);
        entityManager.flush();

        // WHEN
        passwordResetTokenRepository.deleteByTaiKhoan(savedAccount);
        entityManager.flush();
        entityManager.clear(); // Xóa cache để đảm bảo kiểm tra kết quả thật trong DB

        // THEN
        Optional<PasswordResetToken> result = passwordResetTokenRepository.findByToken("old-token");
        assertTrue(result.isEmpty(), "Token phải bị xóa hoàn toàn khỏi DB");
    }
}