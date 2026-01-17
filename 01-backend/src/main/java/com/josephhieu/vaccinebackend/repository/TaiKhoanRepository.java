package com.josephhieu.vaccinebackend.repository;

import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, UUID> {

    @EntityGraph(attributePaths = {"chiTietPhanQuyens", "chiTietPhanQuyens.phanQuyen"})
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);
}
