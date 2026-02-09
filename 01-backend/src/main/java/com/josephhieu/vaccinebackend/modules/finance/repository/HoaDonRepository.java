package com.josephhieu.vaccinebackend.modules.finance.repository;

import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, UUID> {

    // Truy vấn giao dịch khách hàng (XUAT) - JOIN xuyên qua Hồ sơ bệnh án
    @Query("SELECT h FROM HoaDon h " +
            "JOIN HoSoBenhAn hs ON h = hs.hoaDon " +
            "JOIN hs.chiTietDangKyTiem ct " +
            "JOIN ct.benhNhan bn " +
            "WHERE h.loaiHoaDon = 'XUAT' " +
            "AND (:search IS NULL OR bn.tenBenhNhan LIKE %:search% OR bn.sdt LIKE %:search%)")
    Page<HoaDon> findCustomerTransactions(@Param("search") String search, Pageable pageable);

    // Truy vấn giao dịch nhà cung cấp (NHAP) - JOIN qua Lô vắc-xin
    @Query("SELECT h FROM HoaDon h " +
            "JOIN LoVacXin l ON h = l.hoaDon " +
            "JOIN l.nhaCungCap ncc " +
            "WHERE h.loaiHoaDon = 'NHAP' " +
            "AND (:search IS NULL OR ncc.tenNhaCungCap LIKE %:search%)")
    Page<HoaDon> findSupplierTransactions(@Param("search") String search, Pageable pageable);

    // Thống kê doanh thu theo tháng
    @Query("SELECT SUM(h.tongTien) FROM HoaDon h WHERE h.loaiHoaDon = 'XUAT' AND h.trangThai = 1")
    BigDecimal sumTotalRevenue();
}