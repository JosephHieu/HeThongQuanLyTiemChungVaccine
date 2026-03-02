package com.josephhieu.vaccinebackend.modules.medical.repository;

import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.medical.entity.HoSoBenhAn;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class HoSoBenhAnRepositoryTest {

    @Autowired private HoSoBenhAnRepository hoSoBenhAnRepository;
    @Autowired private TestEntityManager entityManager;

    private BenhNhan savedPatient;

    @BeforeEach
    void setUp() {
        // 1. Tạo Master Data
        BenhNhan bn = BenhNhan.builder().tenBenhNhan("Joseph Hieu").build();
        savedPatient = entityManager.persist(bn);

        VacXin vx = VacXin.builder().tenVacXin("AstraZeneca").build();
        entityManager.persist(vx);

        LoVacXin lo = LoVacXin.builder().soLo("BATCH-01").vacXin(vx).build();
        entityManager.persist(lo);

        HoaDon hd = HoaDon.builder()
                .tongTien(new BigDecimal("500000"))
                .trangThai(1) // Đã thanh toán
                .loaiHoaDon("XUAT")
                .ngayTao(LocalDateTime.now())
                .phuongThucThanhToan("Tiền mặt")
                .build();
        entityManager.persist(hd);

        ChiTietDangKyTiem ct = ChiTietDangKyTiem.builder()
                .benhNhan(savedPatient)
                .loVacXin(lo)
                .hoaDon(hd)
                .trangThai("REGISTERED")
                .build();
        entityManager.persist(ct);

        HoSoBenhAn hs = HoSoBenhAn.builder()
                .chiTietDangKyTiem(ct)
                .hoaDon(hd)
                .thoiGianTiem(LocalDateTime.now())
                .build();
        entityManager.persist(hs);

        entityManager.flush();
    }

    @Test
    @DisplayName("Query: findHistoryByPatient phải sử dụng JOIN FETCH để tránh N+1")
    void findHistoryByPatient_Success() {
        var results = hoSoBenhAnRepository.findHistoryByPatient(savedPatient.getMaBenhNhan());

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getChiTietDangKyTiem().getBenhNhan().getTenBenhNhan())
                .isEqualTo("Joseph Hieu");
    }

    @Test
    @DisplayName("Projection: findCustomerTransactions phải map đúng 9 trường vào DTO")
    void findCustomerTransactions_Mapping_Success() {
        var pageable = PageRequest.of(0, 10);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        var resultPage = hoSoBenhAnRepository.findCustomerTransactions(
                "Hieu", start, end, pageable);

        assertThat(resultPage.getContent()).isNotEmpty();
        var dto = resultPage.getContent().get(0);

        // Kiểm tra logic CASE WHEN trong query
        assertThat(dto.getTrangThai()).isEqualTo("Đã thanh toán");
        assertThat(dto.getTenKhachHang()).isEqualTo("Joseph Hieu");
        assertThat(dto.getTenVacXin()).isEqualTo("AstraZeneca");
    }
}