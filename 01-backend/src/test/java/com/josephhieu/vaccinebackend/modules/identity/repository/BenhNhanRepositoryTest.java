package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // Sử dụng cấu hình H2 từ application-test.properties
public class BenhNhanRepositoryTest {

    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Autowired
    private TestEntityManager entityManager; // Công cụ hỗ trợ lưu nhanh để test

    private TaiKhoan savedAccount;

    @BeforeEach
    void setUp() {
        // 1. Tạo và lưu TaiKhoan (vì BenhNhan cần MaTaiKhoan làm FK)
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .tenDangNhap("benhnhantest")
                .email("test@gmail.com")
                .hoTen("Nguyễn Văn Bệnh Nhân")
                .cmnd("123456789")
                .trangThai(true)
                .build();
        savedAccount = entityManager.persist(taiKhoan);

        // 2. Tạo và lưu BenhNhan liên kết với TaiKhoan trên
        BenhNhan benhNhan = BenhNhan.builder()
                .tenBenhNhan("Nguyễn Văn Bệnh Nhân")
                .sdt("0987654321")
                .taiKhoan(savedAccount)
                .build();
        entityManager.persist(benhNhan);
        entityManager.flush();
    }

    @Test
    @DisplayName("Tìm bệnh nhân bằng thực thể TaiKhoan")
    void findByTaiKhoan_Success() {
        Optional<BenhNhan> result = benhNhanRepository.findByTaiKhoan(savedAccount);
        assertTrue(result.isPresent());
        assertEquals("Nguyễn Văn Bệnh Nhân", result.get().getTenBenhNhan());
    }

    @Test
    @DisplayName("Kiểm tra tồn tại hồ sơ bệnh nhân cho tài khoản")
    void existsByTaiKhoan_Success() {
        boolean exists = benhNhanRepository.existsByTaiKhoan(savedAccount);
        assertTrue(exists);
    }

    @Test
    @DisplayName("Tìm bệnh nhân thông qua Tên đăng nhập (Query xuyên bảng)")
    void findByTaiKhoan_TenDangNhap_Success() {
        Optional<BenhNhan> result = benhNhanRepository.findByTaiKhoan_TenDangNhap("benhnhantest");
        assertTrue(result.isPresent());
        assertEquals("0987654321", result.get().getSdt());
    }

    @Test
    @DisplayName("Tìm bệnh nhân thông qua Email tài khoản (Query xuyên bảng)")
    void findByTaiKhoan_Email_Success() {
        Optional<BenhNhan> result = benhNhanRepository.findByTaiKhoan_Email("test@gmail.com");
        assertTrue(result.isPresent());
        assertEquals("benhnhantest", result.get().getTaiKhoan().getTenDangNhap());
    }

    @Test
    @DisplayName("Trả về Optional trống khi không tìm thấy tên đăng nhập")
    void findByTaiKhoan_TenDangNhap_NotFound() {
        Optional<BenhNhan> result = benhNhanRepository.findByTaiKhoan_TenDangNhap("khong_ton_tai");
        assertTrue(result.isEmpty());
    }
}