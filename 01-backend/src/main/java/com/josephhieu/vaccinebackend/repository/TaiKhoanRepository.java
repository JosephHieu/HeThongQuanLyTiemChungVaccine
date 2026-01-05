package com.josephhieu.vaccinebackend.repository;

import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, UUID> {

    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
}
