package com.josephhieu.vaccinebackend.modules.medical.repository;

import com.josephhieu.vaccinebackend.modules.finance.dto.response.CustomerTransactionResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.HoSoBenhAn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HoSoBenhAnRepository extends JpaRepository<HoSoBenhAn, UUID> {

    /**
     * Tìm hồ sơ bệnh án mới nhất của một bệnh nhân dựa trên thời gian tiêm.
     */
    @Query("SELECT hs FROM HoSoBenhAn hs " +
            "JOIN FETCH hs.chiTietDangKyTiem ct " +
            "JOIN FETCH ct.loVacXin l " +
            "JOIN FETCH l.vacXin v " +
            "WHERE ct.benhNhan.maBenhNhan = :maBN " +
            "ORDER BY hs.thoiGianTiem DESC")
    List<HoSoBenhAn> findHistoryByPatient(@Param("maBN") UUID maBN);

    @Query("SELECT new com.josephhieu.vaccinebackend.modules.finance.dto.response.CustomerTransactionResponse(" +
            "hs.thoiGianTiem, " +                   // 1. ngay
            "CAST(hd.maHoaDon AS string), " +       // 2. maHoaDon
            "CAST(v.maVacXin AS string), " +        // 3. maVacXin
            "v.tenVacXin, " +                       // 4. tenVacXin
            "1, " +                                 // 5. soLuong (Integer)
            "bn.tenBenhNhan, " +                    // 6. tenKhachHang
            "hd.tongTien, " +                       // 7. gia
            "(CASE WHEN hd.trangThai = 1 THEN 'Đã thanh toán' " +
            "      WHEN hd.trangThai = 2 THEN 'Đã hủy' " +
            "      ELSE 'Chờ thanh toán' END), " +  // 8. trangThai
            "hd.phuongThucThanhToan" +              // 9. THÊM TRƯỜNG NÀY VÀO ĐÂY (QUAN TRỌNG)
            ") " +
            "FROM HoSoBenhAn hs " +
            "JOIN hs.hoaDon hd " +
            "JOIN hs.chiTietDangKyTiem ct " +
            "JOIN ct.benhNhan bn " +
            "JOIN ct.loVacXin l " +
            "JOIN l.vacXin v " +
            "WHERE hd.loaiHoaDon = 'XUAT' " +
            "AND (:search IS NULL OR LOWER(bn.tenBenhNhan) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CAST(hd.maHoaDon AS string)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (hd.ngayTao BETWEEN :startDate AND :endDate)")
    Page<CustomerTransactionResponse> findCustomerTransactions(
            @Param("search") String search,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
