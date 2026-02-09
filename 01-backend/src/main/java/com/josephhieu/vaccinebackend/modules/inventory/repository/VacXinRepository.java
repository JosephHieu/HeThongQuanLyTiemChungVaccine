package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import org.springframework.data.domain.Page; // Đã sửa import
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacXinRepository extends JpaRepository<VacXin, UUID> {

    Optional<VacXin> findByTenVacXin(String tenVacXin);

    @Query("SELECT new com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse(" +
            "v.maVacXin, " +
            "l.maLo, " +        // Lấy mã UUID thực sự của lô
            "l.soLo, " +        // Lấy số lô cụ thể
            "v.tenVacXin, " +
            "v.phongNguaBenh, " +
            "l.soLuong, " +      // Lấy số lượng của riêng lô đó
            "v.doTuoiTiemChung, " +
            "v.donGia) " +
            "FROM VacXin v " +
            "JOIN LoVacXin l ON v = l.vacXin " + // Dùng JOIN thay vì LEFT JOIN để chỉ hiện vắc-xin có hàng
            "WHERE (:keyword IS NULL OR LOWER(v.tenVacXin) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.phongNguaBenh) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND l.soLuong > 0") // Chỉ hiện những lô còn hàng
    Page<VaccineInfoResponse> searchVaccines(@Param("keyword") String keyword, Pageable pageable);

    // Tìm các vắc-xin mà cột PhongNguaBenh có chứa từ khóa (tên dịch bệnh)
    List<VacXin> findByPhongNguaBenhContainingIgnoreCase(String tenBenh);

    // Cập nhật giá bán niêm yết nhanh (Dùng cho chức năng sửa giá)
    @Modifying
    @Transactional
    @Query("UPDATE VacXin v SET v.donGia = :newPrice WHERE v.maVacXin = :id")
    int updateSellingPrice(@Param("id") UUID id, @Param("newPrice")BigDecimal newPrice);

    // Truy vấn dữ liệu cho bảng "Quản lý giá vắc-xin"
    // Kết hợp thông tin loại vắc-xin để hiển thị đầy đủ
    @Query("SELECT v FROM VacXin v JOIN FETCH v.loaiVacXin")
    Page<VacXin> findAllForPriceManagement(Pageable pageable);

    // Tìm kiếm phân trang theo Tên hoặc Phòng bệnh
    @Query("SELECT v FROM VacXin v WHERE " +
            "(:keyword IS NULL OR LOWER(v.tenVacXin) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(:keyword IS NULL OR LOWER(v.phongNguaBenh) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<VacXin> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}