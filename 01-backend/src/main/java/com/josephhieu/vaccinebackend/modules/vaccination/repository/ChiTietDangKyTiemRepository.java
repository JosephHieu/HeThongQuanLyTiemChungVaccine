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

    boolean existsByBenhNhan_MaBenhNhanAndLichTiemChung_MaLichTiem(UUID patientId, UUID scheduleId);

    // Tìm ds đăng ký của bệnh nhân dựa trên mã bệnh nhân
    List<ChiTietDangKyTiem> findByBenhNhan_MaBenhNhanOrderByLichTiemChung_NgayTiemDesc(UUID maBenhNhan);

    boolean existsByBenhNhan_MaBenhNhanAndLichTiemChung_MaLichTiemAndTrangThaiNot(UUID patientId, UUID scheduleId, String status);

    List<ChiTietDangKyTiem> findByBenhNhan_MaBenhNhanAndTrangThai(UUID maBN, String trangThai);

    /**
     * Kiểm tra trùng lặp dựa trên Lô vắc-xin (Dùng cho luồng Tra cứu/Tiêm lẻ)
     * Vì đăng ký từ tra cứu không có MaLichTiem, ta phải check theo MaLo.
     */
    boolean existsByBenhNhan_MaBenhNhanAndLoVacXin_MaLoAndTrangThai(UUID patientId, UUID batchId, String status);

    /**
     * Tìm ds đăng ký của bệnh nhân.
     * SỬA LẠI: Sắp xếp theo thoiGianCanTiem thay vì LichTiemChung_NgayTiem
     * để tránh lỗi NullPointer hoặc mất dữ liệu khi tiêm lẻ.
     */
    List<ChiTietDangKyTiem> findByBenhNhan_MaBenhNhanOrderByThoiGianCanTiemDesc(UUID maBenhNhan);

}
