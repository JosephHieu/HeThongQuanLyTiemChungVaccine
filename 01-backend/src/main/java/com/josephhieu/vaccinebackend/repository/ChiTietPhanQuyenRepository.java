package com.josephhieu.vaccinebackend.repository;

import com.josephhieu.vaccinebackend.entity.ChiTietPhanQuyen;
import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.entity.id.ChiTietPhanQuyenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChiTietPhanQuyenRepository extends JpaRepository<ChiTietPhanQuyen, ChiTietPhanQuyenId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietPhanQuyen c WHERE c.taiKhoan = :taiKhoan")
    void deleteByTaiKhoan(TaiKhoan taiKhoan);
}
