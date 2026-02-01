package com.josephhieu.vaccinebackend.modules.vaccination.repository;

import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChiTietDangKyTiemRepository extends JpaRepository<ChiTietDangKyTiem, UUID> {

    // Truy vấn danh sách bệnh nhân đã đăng ký của một lịch tiêm (hỗ trợ phân trang cho bảng)
    Page<ChiTietDangKyTiem> findByLichTiemChung_MaLichTiem(UUID maLichTiem, Pageable pageable);

    Page<ChiTietDangKyTiem> findByLichTiemChung_NgayTiem(LocalDate ngayTiem, Pageable pageable);

    /**
     * Tìm các mũi tiêm ĐÃ ĐĂNG KÝ/KÊ ĐƠN nhưng CHƯA TIÊM
     * (Nghĩa là không tồn tại trong bảng HOSOBENHAN)
     */
    @Query("SELECT ct FROM ChiTietDangKyTiem ct " +
            "LEFT JOIN HoSoBenhAn hs ON hs.chiTietDangKyTiem.maChiTietDKTiem = ct.maChiTietDKTiem " +
            "WHERE ct.benhNhan.maBenhNhan = :maBN " +
            "AND hs.maHoSoBenhAn IS NULL " +
            "ORDER BY ct.thoiGianCanTiem ASC")
    List<ChiTietDangKyTiem> findPendingAppointments(@Param("maBN") UUID maBN);

}
