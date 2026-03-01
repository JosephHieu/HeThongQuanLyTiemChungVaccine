package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.ChiTietPhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.entity.id.ChiTietPhanQuyenId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ChiTietPhanQuyenRepositoryTest {

    @Autowired private ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;
    @Autowired private TestEntityManager entityManager;

    private TaiKhoan savedAccount;

    @BeforeEach
    void setUp() {
        // 1. Tạo Tài khoản
        TaiKhoan tk = TaiKhoan.builder()
                .tenDangNhap("user_to_clear")
                .hoTen("User Test")
                .build();
        savedAccount = entityManager.persist(tk);

        // 2. Tạo 2 Quyền khác nhau
        PhanQuyen role1 = PhanQuyen.builder().tenQuyen("Admin").build();
        PhanQuyen role2 = PhanQuyen.builder().tenQuyen("Staff").build();
        entityManager.persist(role1);
        entityManager.persist(role2);

        // 3. Gán cả 2 quyền cho tài khoản này (Bảng trung gian)
        ChiTietPhanQuyen ct1 = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(role1.getMaQuyen(), savedAccount.getMaTaiKhoan()))
                .phanQuyen(role1)
                .taiKhoan(savedAccount)
                .build();

        ChiTietPhanQuyen ct2 = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(role2.getMaQuyen(), savedAccount.getMaTaiKhoan()))
                .phanQuyen(role2)
                .taiKhoan(savedAccount)
                .build();

        entityManager.persist(ct1);
        entityManager.persist(ct2);
        entityManager.flush();
    }

    @Test
    @DisplayName("deleteByTaiKhoan: Xóa sạch tất cả quyền của tài khoản được chỉ định")
    void deleteByTaiKhoan_Success() {
        // Kiểm tra trước khi xóa có 2 bản ghi
        assertEquals(2, chiTietPhanQuyenRepository.count());

        // Thực hiện xóa
        chiTietPhanQuyenRepository.deleteByTaiKhoan(savedAccount);

        // Đồng bộ với DB và xóa cache của EntityManager để kiểm tra kết quả thật
        entityManager.flush();
        entityManager.clear();

        // Sau khi xóa số bản ghi phải là 0
        assertEquals(0, chiTietPhanQuyenRepository.count());
    }
}