package com.josephhieu.vaccinebackend.modules.inventory.repository;

import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacXinRepository extends JpaRepository<VacXin, UUID> {

    // Tìm vắc-xin theo tên để tránh tạo trùng lặp dữ liệu gốc
    Optional<VacXin> findByTenVacXin(String tenVacXin);
}
