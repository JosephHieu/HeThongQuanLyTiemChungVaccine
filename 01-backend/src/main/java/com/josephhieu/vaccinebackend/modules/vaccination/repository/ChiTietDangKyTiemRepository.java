package com.josephhieu.vaccinebackend.modules.vaccination.repository;

import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChiTietDangKyTiemRepository extends JpaRepository<ChiTietDangKyTiem, UUID> {

    // Truy vấn danh sách bệnh nhân đã đăng ký của một lịch tiêm (hỗ trợ phân trang cho bảng)
    Page<ChiTietDangKyTiem> findByLichTiemChung_MaLichTiem(UUID maLichTiem, Pageable pageable);
}
