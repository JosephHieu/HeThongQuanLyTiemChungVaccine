package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.*;
import com.josephhieu.vaccinebackend.modules.identity.entity.id.ChiTietPhanQuyenId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class NhanVienRepositoryTest {

    @Autowired private NhanVienRepository nhanVienRepository;
    @Autowired private TestEntityManager entityManager;

    private final String ROLE_ADMIN = "Administrator";
    private final String ROLE_STAFF = "Staff";

    @BeforeEach
    void setUp() {
        // 1. Tạo các Quyền (PhanQuyen)
        PhanQuyen adminRole = PhanQuyen.builder().tenQuyen(ROLE_ADMIN).build();
        PhanQuyen staffRole = PhanQuyen.builder().tenQuyen(ROLE_STAFF).build();
        entityManager.persist(adminRole);
        entityManager.persist(staffRole);

        // 2. Tạo các Tài khoản (TaiKhoan)
        TaiKhoan tk1 = TaiKhoan.builder().tenDangNhap("admin_user").hoTen("Admin User").build();
        TaiKhoan tk2 = TaiKhoan.builder().tenDangNhap("staff_user").hoTen("Staff User").build();
        entityManager.persist(tk1);
        entityManager.persist(tk2);

        // 3. Gán quyền vào bảng trung gian (ChiTietPhanQuyen)
        ChiTietPhanQuyen ct1 = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(adminRole.getMaQuyen(), tk1.getMaTaiKhoan()))
                .phanQuyen(adminRole).taiKhoan(tk1).build();

        ChiTietPhanQuyen ct2 = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(staffRole.getMaQuyen(), tk2.getMaTaiKhoan()))
                .phanQuyen(staffRole).taiKhoan(tk2).build();

        entityManager.persist(ct1);
        entityManager.persist(ct2);

        // 4. Tạo hồ sơ Nhân viên (NhanVien)
        NhanVien nv1 = NhanVien.builder().tenNhanVien("Nguyen Admin").taiKhoan(tk1).build();
        NhanVien nv2 = NhanVien.builder().tenNhanVien("Le Staff").taiKhoan(tk2).build();
        entityManager.persist(nv1);
        entityManager.persist(nv2);

        entityManager.flush();
    }

    @Test
    @DisplayName("findStaffByRoleName: Phải tìm đúng nhân viên theo tên quyền hạn (JOIN 4 bảng)")
    void findStaffByRoleName_Success() {
        // Thực hiện truy vấn tìm Administrator
        List<NhanVien> admins = nhanVienRepository.findStaffByRoleName(ROLE_ADMIN);

        // Kiểm chứng
        assertEquals(1, admins.size());
        assertEquals("Nguyen Admin", admins.get(0).getTenNhanVien());
        assertEquals("admin_user", admins.get(0).getTaiKhoan().getTenDangNhap());
    }

    @Test
    @DisplayName("findByTaiKhoan_TenDangNhap: Tìm hồ sơ qua username tài khoản")
    void findByTaiKhoan_TenDangNhap_Success() {
        Optional<NhanVien> result = nhanVienRepository.findByTaiKhoan_TenDangNhap("staff_user");

        assertTrue(result.isPresent());
        assertEquals("Le Staff", result.get().getTenNhanVien());
    }

    @Test
    @DisplayName("findStaffByRoleName: Trả về danh sách rỗng khi quyền không tồn tại hoặc không có nhân viên")
    void findStaffByRoleName_Empty() {
        List<NhanVien> result = nhanVienRepository.findStaffByRoleName("NonExistentRole");
        assertTrue(result.isEmpty());
    }
}