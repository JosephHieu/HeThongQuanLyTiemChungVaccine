package com.josephhieu.vaccinebackend.modules.medical.repository;

import com.josephhieu.vaccinebackend.modules.medical.entity.DichBenh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DichBenhRepository extends JpaRepository<DichBenh, UUID> {

    /**
     * Tìm kiếm dịch bệnh theo địa chỉ (Không phân biệt hoa thường).
     * Giúp User lọc xem khu vực mình ở có dịch hay không.
     */
    List<DichBenh> findByDiaChiContainingIgnoreCaseOrderByThoiDiemKhaoSatDesc(String diaChi);

    /**
     * Tìm kiếm theo tên dịch bệnh.
     */
    List<DichBenh> findByTenDichBenhContainingIgnoreCaseOrderByThoiDiemKhaoSatDesc(String tenBenh);

    /**
     * Lấy danh sách dịch bệnh mới nhất dựa trên thời điểm khảo sát.
     */
    List<DichBenh> findAllByOrderByThoiDiemKhaoSatDesc();
}
