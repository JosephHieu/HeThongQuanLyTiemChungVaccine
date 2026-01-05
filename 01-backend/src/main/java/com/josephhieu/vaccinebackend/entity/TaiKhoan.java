package com.josephhieu.vaccinebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TAIKHOAN")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiKhoan {

    /**
     * Mã định danh duy nhất cho tài khoản.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaTaiKhoan", length = 36, updatable = false, nullable = false)
    private UUID maTaiKhoan;

    /**
     * Tên đăng nhập của người dùng.
     */
    @Column(name = "TenDangNhap", length = 255, unique = true)
    private String tenDangNhap;

    /**
     * Mật khẩu đăng nhập (cần được mã hóa trước khi lưu).
     */
    @Column(name = "MatKhau", length = 255)
    private String matKhau;

    /**
     * Họ và tên đầy đủ của nhân viên hoặc người dùng.
     */
    @Column(name = "HoTen", length = 255)
    private String hoTen;

    /**
     * Số chứng minh nhân dân hoặc căn cước công dân.
     */
    @Column(name = "CMND", length = 255)
    private String cmnd;

    /**
     * Địa chỉ nơi ở hiện tại.
     */
    @Column(name = "NoiO", length = 255)
    private String noiO;

    /**
     * Thông tin mô tả thêm về người dùng.
     */
    @Column(name = "MoTa", columnDefinition = "TEXT")
    private String moTa;

    /**
     * Địa chỉ thư điện tử liên lạc.
     */
    @Column(name = "Email", length = 255)
    private String email;

    /**
     * Tập hợp các quyền hạn được gán cho tài khoản này qua bảng trung gian.
     */
    @OneToMany(mappedBy = "taiKhoan")
    @Builder.Default
    private Set<ChiTietPhanQuyen> chiTietPhanQuyens = new HashSet<>();
}
