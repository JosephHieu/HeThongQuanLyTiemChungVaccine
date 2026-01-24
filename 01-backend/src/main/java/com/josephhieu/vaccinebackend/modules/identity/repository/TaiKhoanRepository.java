package com.josephhieu.vaccinebackend.modules.identity.repository;

import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, UUID> {

    @Query("SELECT DISTINCT t FROM TaiKhoan t LEFT JOIN FETCH t.chiTietPhanQuyens WHERE t.tenDangNhap = :username")
    Optional<TaiKhoan> findByTenDangNhap(@Param("username") String username);

    // Dùng EntityGraph để lấy kèm Roles khi tìm tất cả, tránh lỗi 500 khi map DTO
    @EntityGraph(attributePaths = {"chiTietPhanQuyens", "chiTietPhanQuyens.phanQuyen"})
    Page<TaiKhoan> findAll(Pageable pageable);

    boolean existsByTenDangNhap(String tenDangNhap);
}
