package com.josephhieu.vaccinebackend.modules.finance.repository;

import com.josephhieu.vaccinebackend.modules.finance.dto.response.CustomerTransactionResponse;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class HoaDonRepositoryTest {

    @Autowired private HoaDonRepository hoaDonRepository;
    @Autowired private VacXinRepository vacXinRepository;
    @Autowired private BenhNhanRepository benhNhanRepository;
    @Autowired private LoVacXinRepository loVacXinRepository;
    @Autowired private ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        // 1. Tạo Vắc-xin
        VacXin v = vacXinRepository.save(VacXin.builder()
                .tenVacXin("AstraZeneca").donGia(new BigDecimal("200000")).build());

        // 2. Tạo Lô Vắc-xin
        LoVacXin lo = loVacXinRepository.save(LoVacXin.builder()
                .soLo("BATCH-001").vacXin(v).soLuong(100).build());

        // 3. Tạo Bệnh nhân
        BenhNhan bn = benhNhanRepository.save(BenhNhan.builder()
                .tenBenhNhan("Joseph Hieu").sdt("0909123456").build());

        // 4. Tạo Hóa đơn XUẤT (Đã thanh toán)
        HoaDon hd1 = hoaDonRepository.save(HoaDon.builder()
                .tongTien(new BigDecimal("200000")).loaiHoaDon("XUAT")
                .trangThai(1).ngayTao(now).build());

        // 5. Liên kết Chi tiết Đăng ký tiêm (Gốc để truy vấn Customer Transactions)
        chiTietDangKyTiemRepository.save(ChiTietDangKyTiem.builder()
                .benhNhan(bn).loVacXin(lo).hoaDon(hd1).trangThai("REGISTERED").build());
    }

    @Test
    @DisplayName("findCustomerTransactions: Lấy đúng DTO khi JOIN 5 bảng")
    void findCustomerTransactions_Success() {
        Page<CustomerTransactionResponse> result = hoaDonRepository.findCustomerTransactions(
                "Joseph", now.minusDays(1), now.plusDays(1), PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals("Joseph Hieu", result.getContent().get(0).getTenKhachHang());
        assertEquals("Đã thanh toán", result.getContent().get(0).getTrangThai());
    }

    @Test
    @DisplayName("sumRevenueByPeriod: Tính tổng doanh thu chính xác")
    void sumRevenueByPeriod_CorrectCalculation() {
        BigDecimal revenue = hoaDonRepository.sumRevenueByPeriod(now.minusHours(1), now.plusHours(1));

        // So sánh 200000.00 với BigDecimal.ZERO hoặc giá trị mock
        assertNotNull(revenue);
        assertEquals(0, new BigDecimal("200000").compareTo(revenue));
    }

    @Test
    @DisplayName("countByTrangThaiAndLoaiHoaDon: Đếm số hóa đơn chờ")
    void countPendingInvoices_Success() {
        // Tạo thêm 1 hóa đơn chờ
        hoaDonRepository.save(HoaDon.builder().loaiHoaDon("XUAT").trangThai(0).build());

        long count = hoaDonRepository.countByTrangThaiAndLoaiHoaDon(0, "XUAT");
        assertEquals(1, count);
    }
}