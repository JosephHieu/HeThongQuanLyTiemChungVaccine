package com.josephhieu.vaccinebackend.modules.medical.repository;

import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoaiPhanHoiRepository extends JpaRepository<LoaiPhanHoi, UUID> {

    // Tìm loại phản hồi theo tên (ví dụ: "Phản hồi sau tiêm")
    Optional<LoaiPhanHoi> findByTenLoaiPhanHoi(String tenLoaiPhanHoi);
}