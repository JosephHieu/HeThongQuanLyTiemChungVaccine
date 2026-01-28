package com.josephhieu.vaccinebackend.modules.vaccination.repository;

import com.josephhieu.vaccinebackend.modules.vaccination.entity.LichTiemChung;
// PHẢI dùng import này của Spring Data
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repository quản lý thực thể LichTiemChung.
 * Đã sửa lỗi import Page để hỗ trợ phân trang chuẩn Spring Data JPA.
 */
@Repository
public interface LichTiemChungRepository extends JpaRepository<LichTiemChung, UUID> {

    /**
     * Tìm kiếm lịch tiêm chủng theo khoảng thời gian và địa điểm.
     * Hỗ trợ phân trang để hiển thị lên bảng dữ liệu.
     */
    Page<LichTiemChung> findByNgayTiemBetweenAndDiaDiemContaining(
            LocalDate startDate,
            LocalDate endDate,
            String diaDiem,
            Pageable pageable
    );

    /**
     * Đếm số lượng người đã đăng ký thực tế cho một lịch tiêm.
     */
    @Query("SELECT COUNT(d) FROM ChiTietDangKyTiem d WHERE d.lichTiemChung.maLichTiem = :maLichTiem")
    long countRegisteredPatients(@Param("maLichTiem") UUID maLichTiem);

    @Query("SELECT l FROM LichTiemChung l " +
            "WHERE (:startDate IS NULL OR l.ngayTiem >= :startDate) " +
            "AND (:endDate IS NULL OR l.ngayTiem <= :endDate) " +
            "AND (:diaDiem IS NULL OR l.diaDiem LIKE %:diaDiem%)")
    Page<LichTiemChung> searchSchedules(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("diaDiem") String diaDiem,
                                        Pageable pageable);
}