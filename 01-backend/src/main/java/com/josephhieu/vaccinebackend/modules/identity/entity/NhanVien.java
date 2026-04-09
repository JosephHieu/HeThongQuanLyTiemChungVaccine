package com.josephhieu.vaccinebackend.modules.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Thực thể ánh xạ đến bảng NHANVIEN trong cơ sở dữ liệu.
 * Đại diện cho hồ sơ nhân viên y tế, quản kho, tài chính và hỗ trợ.
 * * @author Joseph Hieu
 */
@Entity
@Table(name = "NHANVIEN")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    /**
     * Mã định danh duy nhất của nhân viên (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaNhanVien", length = 36, nullable = false, updatable = false)
    private UUID maNhanVien;

    /**
     * Tên đầy đủ của nhân viên.
     */
    @Column(name = "TenNhanVien", length = 255)
    private String tenNhanVien;

    /**
     * Năm sinh của nhân viên.
     */
    @Column(name = "NamSinh")
    private Integer namSinh;

    /**
     * Số điện thoại liên lạc.
     */
    @Column(name = "SDT", length = 255)
    private String sdt;

    /**
     * Quan hệ 1-1 nối với tài khoản đăng nhập hệ thống.
     */
    @OneToOne
    @JoinColumn(name = "MaTaiKhoan", referencedColumnName = "MaTaiKhoan")
    private TaiKhoan taiKhoan;
}
