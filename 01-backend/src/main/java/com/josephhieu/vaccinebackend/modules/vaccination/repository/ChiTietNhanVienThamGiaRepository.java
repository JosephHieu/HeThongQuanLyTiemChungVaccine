package com.josephhieu.vaccinebackend.modules.vaccination.repository;


import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietNhanVienThamGia;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.id.NhanVienThamGiaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Quản lý dữ liệu nhân viên tham gia các đợt tiêm chủng.
 */
@Repository
public interface ChiTietNhanVienThamGiaRepository extends JpaRepository<ChiTietNhanVienThamGia, NhanVienThamGiaId> {
}
