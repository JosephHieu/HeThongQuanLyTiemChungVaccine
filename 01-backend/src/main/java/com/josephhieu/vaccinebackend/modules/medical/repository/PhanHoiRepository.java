package com.josephhieu.vaccinebackend.modules.medical.repository;

import com.josephhieu.vaccinebackend.modules.medical.entity.PhanHoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhanHoiRepository extends JpaRepository<PhanHoi, UUID> {

    // Tìm tất cả phản hồi của một bệnh nhân cụ thể (dùng cho trang lịch sử của User)
    List<PhanHoi> findByBenhNhan_MaBenhNhanOrderByThoiGianTiemDesc(UUID maBenhNhan);

    // Tìm phản hồi theo trạng thái (dùng cho Admin lọc phản hồi mới - status = 0)
    List<PhanHoi> findByTrangThai(Integer trangThai);
}
