package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.NhaCungCap;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Sử dụng H2 Database
public class LoVacXinRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoVacXinRepository loVacXinRepository;

    private VacXin astra;
    private LoaiVacXin loaiSong;

    @BeforeEach
    void setUp() {
        // 1. Tạo loại vắc-xin
        loaiSong = LoaiVacXin.builder().tenLoaiVacXin("Vắc-xin sống").build();
        entityManager.persist(loaiSong);

        // 2. Tạo vắc-xin danh mục
        astra = VacXin.builder()
                .tenVacXin("AstraZeneca")
                .loaiVacXin(loaiSong)
                .hanSuDung(LocalDate.now().plusMonths(6))
                .build();
        entityManager.persist(astra);

        // 3. Tạo Nhà cung cấp
        NhaCungCap ncc = NhaCungCap.builder().tenNhaCungCap("VNVC Supplier").build();
        entityManager.persist(ncc);

        // 4. Tạo các lô hàng mẫu
        LoVacXin lo1 = LoVacXin.builder()
                .soLo("BATCH-001")
                .vacXin(astra)
                .nhaCungCap(ncc)
                .soLuong(100)
                .giaNhap(new BigDecimal("100000.00"))
                .nuocSanXuat("Anh")
                .tinhTrang("Còn")
                .build();

        LoVacXin lo2 = LoVacXin.builder()
                .soLo("BATCH-002")
                .vacXin(astra)
                .nhaCungCap(ncc)
                .soLuong(50)
                .giaNhap(new BigDecimal("120000.00"))
                .nuocSanXuat("Anh")
                .tinhTrang("Còn")
                .build();

        entityManager.persist(lo1);
        entityManager.persist(lo2);
        entityManager.flush();
    }

    @Test
    @DisplayName("Search: Tìm kiếm theo tên vắc-xin thành công")
    void searchInventory_ByName_ShouldReturnCorrectBatch() {
        var pageable = PageRequest.of(0, 10);
        var result = loVacXinRepository.searchInventory("name", "Astra", pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getVacXin().getTenVacXin()).isEqualTo("AstraZeneca");
    }

    @Test
    @DisplayName("Finance: Tính tổng giá trị vốn hàng tồn kho")
    void getTotalInventoryValue_ShouldReturnCorrectSum() {
        // (100 * 100,000) + (50 * 120,000) = 10,000,000 + 6,000,000 = 16,000,000
        BigDecimal totalValue = loVacXinRepository.getTotalInventoryValue();

        assertThat(totalValue.compareTo(new BigDecimal("16000000.00"))).isEqualTo(0);
    }

    @Test
    @DisplayName("Expiry: Tìm các lô sắp hết hạn")
    void findExpiringBatches_ShouldReturnList() {
        LocalDate targetDate = LocalDate.now().plusMonths(7);
        List<LoVacXin> expiring = loVacXinRepository.findExpiringBatches(targetDate);

        assertThat(expiring).isNotEmpty();
        assertThat(expiring.get(0).getVacXin().getTenVacXin()).isEqualTo("AstraZeneca");
    }

    @Test
    @DisplayName("Audit: Kiểm tra tồn tại lô hàng theo vắc-xin")
    void existsByVacXin_MaVacXin_ShouldReturnTrue() {
        boolean exists = loVacXinRepository.existsByVacXin_MaVacXin(astra.getMaVacXin());
        assertThat(exists).isTrue();
    }
}