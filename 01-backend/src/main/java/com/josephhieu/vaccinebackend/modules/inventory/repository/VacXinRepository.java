package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse;
import org.springframework.data.domain.Page; // Đã sửa import
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacXinRepository extends JpaRepository<VacXin, UUID> {

    Optional<VacXin> findByTenVacXin(String tenVacXin);

    @Query("SELECT new com.josephhieu.vaccinebackend.modules.vaccination.dto.response.VaccineInfoResponse(" +
            "v.maVacXin, " +
            "MAX(l.soLo), " +
            "v.tenVacXin, " +
            "v.phongNguaBenh, " +
            "CAST(SUM(COALESCE(l.soLuong, 0)) AS integer), " +
            "v.doTuoiTiemChung, " + // Đảm bảo thứ tự này khớp với DTO
            "v.donGia) " +
            "FROM VacXin v " +
            "LEFT JOIN LoVacXin l ON v = l.vacXin " +
            "WHERE (:keyword IS NULL OR LOWER(v.tenVacXin) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.phongNguaBenh) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "GROUP BY v.maVacXin, v.tenVacXin, v.phongNguaBenh, v.doTuoiTiemChung, v.donGia")
    Page<VaccineInfoResponse> searchVaccines(@Param("keyword") String keyword, Pageable pageable);
}