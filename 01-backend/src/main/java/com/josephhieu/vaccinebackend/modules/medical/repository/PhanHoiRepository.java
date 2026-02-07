package com.josephhieu.vaccinebackend.modules.medical.repository;

import com.josephhieu.vaccinebackend.modules.medical.entity.PhanHoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhanHoiRepository extends JpaRepository<PhanHoi, UUID> {

    /**
     * Lấy lịch sử phản hồi của một bệnh nhân cụ thể.
     * @param maBenhNhan ID của bệnh nhân.
     * @return Danh sách phản hồi xếp từ mới nhất đến cũ nhất.
     */
    List<PhanHoi> findByBenhNhan_MaBenhNhanOrderByThoiGianTiemDesc(UUID maBenhNhan);

    // Tìm phản hồi theo trạng thái (dùng cho Admin lọc phản hồi mới - status = 0)
    List<PhanHoi> findByTrangThai(Integer trangThai);

    /**
     * Hỗ trợ lấy phản hồi theo Tài khoản đăng nhập (Dùng cho User Portal).
     * Truy xuất xuyên suốt: PhanHoi -> BenhNhan -> TaiKhoan.
     */
    List<PhanHoi> findByBenhNhan_TaiKhoan_MaTaiKhoanOrderByMaPhanHoiDesc(UUID maTaiKhoan);

    /**
     * Lấy tất cả phản hồi dành cho Admin xử lý (Có phân trang nếu số lượng lớn).
     */
    Page<PhanHoi> findAllByOrderByTrangThaiAsc(Pageable pageable);

    /**
     * Đếm số lượng phản hồi mới (Trạng thái = 0) để Admin biết số lượng việc cần làm.
     */
    long countByTrangThai(Integer trangThai);

    /**
     * Truy vấn lịch sử phản hồi dựa trên Tên đăng nhập của tài khoản.
     * Luồng đi: PhanHoi -> BenhNhan -> TaiKhoan -> tenDangNhap
     */
    List<PhanHoi> findByBenhNhan_TaiKhoan_TenDangNhapOrderByNgayTaoDesc(String tenDangNhap);

    /**
     * Dành cho Administrator: Lấy toàn bộ danh sách phản hồi trong hệ thống.
     * Mới nhất xếp trên cùng.
     */
    List<PhanHoi> findAllByOrderByNgayTaoDesc();
}
