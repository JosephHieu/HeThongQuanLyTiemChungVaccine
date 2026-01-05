package com.josephhieu.vaccinebackend.repository;

import com.josephhieu.vaccinebackend.entity.ChiTietPhanQuyen;
import com.josephhieu.vaccinebackend.entity.id.ChiTietPhanQuyenId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietPhanQuyenRepository extends JpaRepository<ChiTietPhanQuyen, ChiTietPhanQuyenId> {
}
