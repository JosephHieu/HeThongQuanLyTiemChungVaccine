package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.PhieuXuat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PhieuXuatRepository extends JpaRepository<PhieuXuat, UUID> {

    // Tìm kiếm các phiếu xuất của một lô cụ thể (nếu cần xem lịch sử)
    List<PhieuXuat> findByLoVacXin_MaLo(UUID maLo);

    boolean existsBySoPhieuXuat(String soPhieuXuat);

    // Lọc theo khoảng thời gian và phân trang
    Page<PhieuXuat> findByNgayXuatBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
