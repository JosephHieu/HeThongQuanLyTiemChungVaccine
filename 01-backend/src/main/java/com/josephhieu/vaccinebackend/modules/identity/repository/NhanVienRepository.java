package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, UUID> {

    Optional<NhanVien> findByTaiKhoan(TaiKhoan taiKhoan);

    Optional<NhanVien> findByTaiKhoan_TenDangNhap(String username);

    /**
     * Truy vấn tìm nhân viên dựa trên tên quyền hạn gắn với tài khoản của họ.
     * Luồng: NhanVien -> TaiKhoan -> ChiTietPhanQuyen -> PhanQuyen
     */
    @Query("SELECT nv FROM NhanVien nv " +
            "JOIN nv.taiKhoan tk " +
            "JOIN tk.chiTietPhanQuyens ctpq " +
            "JOIN ctpq.phanQuyen pq " +
            "WHERE pq.tenQuyen = :roleName")
    List<NhanVien> findStaffByRoleName(@Param("roleName") String roleName);
}
