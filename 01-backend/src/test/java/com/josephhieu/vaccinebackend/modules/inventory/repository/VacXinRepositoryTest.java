package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class VacXinRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VacXinRepository vacXinRepository;

    private VacXin savedVacXin;

    @BeforeEach
    void setUp() {
        // 1. Tạo loại vắc-xin
        LoaiVacXin loai = LoaiVacXin.builder().tenLoaiVacXin("Vắc-xin bất hoạt").build();
        entityManager.persist(loai);

        // 2. Tạo danh mục vắc-xin
        savedVacXin = VacXin.builder()
                .tenVacXin("Vero Cell")
                .phongNguaBenh("Covid-19")
                .doTuoiTiemChung("18-60 tuổi")
                .donGia(new BigDecimal("250000"))
                .loaiVacXin(loai)
                .build();
        entityManager.persist(savedVacXin);

        // 3. Tạo lô hàng còn tồn (Sẽ hiển thị)
        LoVacXin loCon = LoVacXin.builder()
                .soLo("VC-001")
                .vacXin(savedVacXin)
                .soLuong(50)
                .build();
        entityManager.persist(loCon);

        // 4. Tạo lô hàng đã hết (Sẽ bị filter loại bỏ)
        LoVacXin loHet = LoVacXin.builder()
                .soLo("VC-002")
                .vacXin(savedVacXin)
                .soLuong(0)
                .build();
        entityManager.persist(loHet);

        entityManager.flush();
    }

    @Test
    @DisplayName("Search: Chỉ lấy những vắc-xin có lô hàng còn số lượng > 0")
    void searchVaccines_ShouldOnlyReturnAvailableBatches() {
        var pageable = PageRequest.of(0, 10);

        // Test tìm kiếm với từ khóa
        var result = vacXinRepository.searchVaccines("Vero", pageable);

        // Assert: Chỉ có 1 lô VC-001 thỏa mãn (soLuong > 0)
        assertThat(result.getContent()).hasSize(1);
        VaccineInfoResponse dto = result.getContent().get(0);

        assertThat(dto.getSoLo()).isEqualTo("VC-001");
        assertThat(dto.getSoLuongLieu()).isEqualTo(50);
        assertThat(dto.getTenVacXin()).isEqualTo("Vero Cell");
    }

    @Test
    @DisplayName("Update: Cập nhật giá bán niêm yết bằng Modifying Query")
    void updateSellingPrice_ShouldUpdateDatabase() {
        BigDecimal newPrice = new BigDecimal("300000.00");

        int updatedRows = vacXinRepository.updateSellingPrice(savedVacXin.getMaVacXin(), newPrice);

        assertThat(updatedRows).isEqualTo(1);

        // Clear cache để lấy dữ liệu mới nhất từ DB
        entityManager.clear();
        VacXin updated = entityManager.find(VacXin.class, savedVacXin.getMaVacXin());
        assertThat(updated.getDonGia().compareTo(newPrice)).isEqualTo(0);
    }

    @Test
    @DisplayName("Find: Tìm kiếm theo phòng bệnh không phân biệt hoa thường")
    void findByPhongNguaBenh_ShouldWork() {
        var results = vacXinRepository.findByPhongNguaBenhContainingIgnoreCase("covid");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTenVacXin()).isEqualTo("Vero Cell");
    }
}