package com.josephhieu.vaccinebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Thực thể ánh xạ đến bảng DICHBENH.
 * Lưu trữ thông tin chi tiết về khảo sát dịch tễ và các mầm bệnh cần phòng ngừa.
 */
@Entity
@Table(name = "DICHBENH")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DichBenh {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaDichBenh", length = 36, nullable = false, updatable = false)
    private UUID maDichBenh;

    /**
     * Nhân viên thực hiện khảo sát dịch bệnh này.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVien")
    private NhanVien nhanVien;

    @Column(name = "TenDichBenh", length = 255, nullable = false)
    private String tenDichBenh;

    @Column(name = "DuongLayNhiem", columnDefinition = "TEXT")
    private String duongLayNhiem;

    @Column(name = "TacHaiSucKhoe", columnDefinition = "TEXT")
    private String tacHaiSucKhoe;

    @Column(name = "SoNguoiBiNhiem")
    private Integer soNguoiBiNhiem;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Column(name = "GhiChu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ThoiDiemKhaoSat")
    private LocalDate thoiDiemKhaoSat;
}
