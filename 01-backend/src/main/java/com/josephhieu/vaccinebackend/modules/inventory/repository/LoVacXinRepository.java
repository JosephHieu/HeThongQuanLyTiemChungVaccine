package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
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
public interface LoVacXinRepository extends JpaRepository<LoVacXin, UUID> {

    // Tìm kiếm xuyên bảng: LoVacXin -> VacXin -> LoaiVacXin
    @Query("SELECT l FROM LoVacXin l JOIN l.vacXin v JOIN v.loaiVacXin lv WHERE " +
            "(:criteria = 'name' AND LOWER(v.tenVacXin) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(:criteria = 'type' AND LOWER(lv.tenLoaiVacXin) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(:criteria = 'origin' AND LOWER(l.nuocSanXuat) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(:criteria = 'batch' AND LOWER(l.soLo) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(:search IS NULL OR :search = '')")
    Page<LoVacXin> searchInventory(@Param("criteria") String criteria,
                                   @Param("search") String search,
                                   Pageable pageable);

    // Thống kê tổng tồn kho
    @Query("SELECT SUM(l.soLuong) FROM LoVacXin l")
    Long countTotalVaccines();

    // Tìm các lô sắp hết hạn dựa trên VacXin
    @Query("SELECT l FROM LoVacXin l JOIN l.vacXin v WHERE v.hanSuDung <= :targetDate")
    List<LoVacXin> findExpiringBatches(@Param("targetDate") LocalDate targetDate);

    boolean existsBySoLo(String soLo);
}
