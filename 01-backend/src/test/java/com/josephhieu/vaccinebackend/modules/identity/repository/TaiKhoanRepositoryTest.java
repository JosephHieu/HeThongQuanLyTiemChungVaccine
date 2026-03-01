package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.*;
import com.josephhieu.vaccinebackend.modules.identity.entity.id.ChiTietPhanQuyenId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TaiKhoanRepositoryTest {

    @Autowired private TaiKhoanRepository taiKhoanRepository;
    @Autowired private TestEntityManager entityManager;

    private TaiKhoan savedUser;
    private UUID adminRoleId;

    @BeforeEach
    void setUp() {
        // 1. Tạo quyền Admin
        PhanQuyen adminRole = PhanQuyen.builder()
                .tenQuyen("Administrator")
                .build();
        adminRole = entityManager.persist(adminRole);
        adminRoleId = adminRole.getMaQuyen();

        // 2. Tạo tài khoản
        TaiKhoan tk = TaiKhoan.builder()
                .tenDangNhap("joseph_hieu")
                .hoTen("Joseph Hieu")
                .email("hieu@gmail.com")
                .trangThai(true)
                .build();
        savedUser = entityManager.persist(tk);

        // 3. Gán quyền cho tài khoản (Bảng trung gian)
        ChiTietPhanQuyen ct = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(adminRoleId, savedUser.getMaTaiKhoan()))
                .phanQuyen(adminRole)
                .taiKhoan(savedUser)
                .build();
        entityManager.persist(ct);

        entityManager.flush();
    }

    @Test
    @DisplayName("findByTenDangNhap: Tìm kiếm và FETCH quyền hạn")
    void findByTenDangNhap_Success() {
        // Ép Hibernate đẩy dữ liệu xuống DB và xóa cache để lần truy vấn sau sẽ đọc từ DB thật
        entityManager.flush();
        entityManager.clear();

        // WHEN
        Optional<TaiKhoan> result = taiKhoanRepository.findByTenDangNhap("joseph_hieu");

        // THEN
        assertTrue(result.isPresent());
        assertEquals("Joseph Hieu", result.get().getHoTen());

        // Đã dùng FETCH JOIN nên danh sách quyền phải có dữ liệu (size > 0)
        assertNotNull(result.get().getChiTietPhanQuyens());
        assertEquals(1, result.get().getChiTietPhanQuyens().size());
    }

    @Test
    @DisplayName("findAllWithFilter: Lọc theo mã quyền hạn")
    void findAllWithFilter_ByRole_Success() {
        entityManager.flush();
        entityManager.clear();

        // WHEN
        Page<TaiKhoan> result = taiKhoanRepository.findAllWithFilter(null, adminRoleId, PageRequest.of(0, 10));

        // THEN
        assertEquals(1, result.getTotalElements());

        // Kiểm tra xem User lấy ra có đúng là có Role Admin không
        boolean hasAdminRole = result.getContent().get(0).getChiTietPhanQuyens()
                .stream()
                .anyMatch(ct -> ct.getPhanQuyen().getMaQuyen().equals(adminRoleId));

        assertTrue(hasAdminRole, "Tài khoản phải chứa quyền Admin tương ứng");
    }
}