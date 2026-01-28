package com.josephhieu.vaccinebackend.modules.vaccination.repository;


import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietNhanVienThamGia;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.LichTiemChung;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.id.NhanVienThamGiaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Quản lý dữ liệu nhân viên tham gia các đợt tiêm chủng.
 */
@Repository
public interface ChiTietNhanVienThamGiaRepository extends JpaRepository<ChiTietNhanVienThamGia, NhanVienThamGiaId> {

    // 1. Lấy danh sách nhân viên tham gia theo mã lịch tiêm
    List<ChiTietNhanVienThamGia> findByLichTiemChung_MaLichTiem(UUID maLichTiem);

    // 2. Xóa tất cả nhân viên tham gia của một lịch tiêm cụ thể
    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietNhanVienThamGia c WHERE c.lichTiemChung = :schedule")
    void deleteByLichTiemChung(@Param("schedule") LichTiemChung schedule);

    // Gợi ý thêm: Xóa theo ID sẽ tiện lợi hơn khi bạn chỉ có UUID ở Controller/Service
    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietNhanVienThamGia c WHERE c.lichTiemChung.maLichTiem = :maLichTiem")
    void deleteByMaLichTiem(@Param("maLichTiem") UUID maLichTiem);
}
