package com.josephhieu.vaccinebackend.modules.finance.repository;

import com.josephhieu.vaccinebackend.modules.finance.dto.response.CustomerTransactionResponse;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, UUID> {

    // 1. Truy vấn giao dịch khách hàng (XUAT) - Trả về DTO trực tiếp để đạt hiệu năng cao nhất
    @Query("SELECT new com.josephhieu.vaccinebackend.modules.finance.dto.response.CustomerTransactionResponse(" +
            "h.ngayTao, " +
            "CAST(h.maHoaDon AS string), " +
            "CAST(v.maVacXin AS string), " +
            "v.tenVacXin, " +
            "1, " + // Số lượng mặc định là 1 mũi tiêm
            "bn.tenBenhNhan, " +
            "h.tongTien, " +
            "(CASE WHEN h.trangThai = 1 THEN 'Đã thanh toán' " +
            "      WHEN h.trangThai = 2 THEN 'Đã hủy' " +
            "      ELSE 'Chờ thanh toán' END), " +
            "h.phuongThucThanhToan" +
            ") " +
            "FROM ChiTietDangKyTiem ct " + // Lấy ChiTietDangKyTiem làm gốc
            "JOIN ct.hoaDon h " +         // Join sang HoaDon (luôn có vì bạn tạo lúc đăng ký)
            "JOIN ct.benhNhan bn " +      // Join sang BenhNhan
            "JOIN ct.loVacXin l " +       // Join sang LoVacXin
            "JOIN l.vacXin v " +          // Join sang VacXin để lấy tên và mã
            "WHERE h.loaiHoaDon = 'XUAT' " +
            "AND (:search IS NULL OR LOWER(bn.tenBenhNhan) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(bn.sdt) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(CAST(h.maHoaDon AS string)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (h.ngayTao BETWEEN :startDate AND :endDate)")
    Page<CustomerTransactionResponse> findCustomerTransactions(
            @Param("search") String search,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 2. Truy vấn giao dịch nhà cung cấp (NHAP) - Dành cho Tab Nhập hàng NCC
    @Query("SELECT h FROM HoaDon h " +
            "JOIN LoVacXin l ON h = l.hoaDon " +
            "JOIN l.nhaCungCap ncc " +
            "WHERE h.loaiHoaDon = 'NHAP' " +
            "AND (:search IS NULL OR LOWER(ncc.tenNhaCungCap) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<HoaDon> findSupplierTransactions(@Param("search") String search, Pageable pageable);

    // 3. Thống kê doanh thu thực tế (Chỉ tính hóa đơn ĐÃ THANH TOÁN)
    @Query("SELECT SUM(h.tongTien) FROM HoaDon h " +
            "WHERE h.loaiHoaDon = 'XUAT' " +
            "AND h.trangThai = 1 " +
            "AND h.ngayTao BETWEEN :start AND :end")
    BigDecimal sumRevenueByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    long countByTrangThai(Integer trangThai);
}