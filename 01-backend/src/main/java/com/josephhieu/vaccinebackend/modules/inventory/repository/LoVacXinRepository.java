package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    // Câu lện JPQL để tính tổng cột soLuong
    @Query("SELECT SUM(l.soLuong) FROM LoVacXin l")
    Long getTotalDoses();

    // Tính tông theo điều hiện tìm kiếm
    @Query("SELECT SUM(l.soLuong) FROM LoVacXin l WHERE l.tinhTrang = 'Còn'")
    Long getTotalAvailableDoses();

    Optional<LoVacXin> findFirstByVacXin_MaVacXinAndSoLuongGreaterThanOrderByNgayNhanAsc(UUID maVacXin, Integer minSoLuong);

    // 1. Tính tổng giá trị vốn hàng tồn kho hiện tại
    // Công thức: sum (soLuong \times giaNhap)
    @Query("SELECT COALESCE(SUM(l.soLuong * l.giaNhap), 0.0) FROM LoVacXin l WHERE l.soLuong > 0")
    BigDecimal getTotalInventoryValue();

    // 2. Lấy giá nhập trung bình của một loại vắc-xin (Để hỗ trợ đặt giá bán)
    @Query("SELECT AVG(l.giaNhap) FROM LoVacXin l WHERE l.vacXin.maVacXin = :vaccineId")
    BigDecimal getAverageImportPrice(@Param("vaccineId") UUID vaccineId);

    // 3. Tìm lô có giá nhập cao nhất/thấp nhất của 1 loại vắc-xin
    List<LoVacXin> findByVacXinMaVacXinOrderByGiaNhapDesc(UUID maVacXin);

    /**
     * Kiểm tra xem có bất kỳ lô vắc-xin nào đang liên kết với mã vắc-xin này không.
     * Sử dụng để ngăn chặn việc xóa vắc-xin đang có dữ liệu tồn kho.
     * * @param maVacXin ID của vắc-xin cần kiểm tra
     * @return true nếu tồn tại ít nhất 1 lô hàng, ngược lại false
     */
    boolean existsByVacXin_MaVacXin(UUID maVacXin);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LoVacXin l WHERE l.maLo = :id")
    Optional<LoVacXin> findByIdWithLock(@Param("id") UUID id);

}
