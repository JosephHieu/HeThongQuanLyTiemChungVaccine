package com.josephhieu.vaccinebackend.modules.identity.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Thực thể ánh xạ đến bảng BENHNHAN trong cơ sở dữ liệu.
 * Lưu trữ thông tin chi tiết của bệnh nhân và người giám hộ.
 * Đã cập nhật trường Giới tính để phù hợp với màn hình hồ sơ bệnh án.
 * * @author Joseph Hieu
 * @version 1.1
 */
@Entity
@Table(name = "BENHNHAN")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenhNhan {

    /**
     * Mã định danh duy nhất của bệnh nhân (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaBenhNhan", length = 36, nullable = false, updatable = false)
    private UUID maBenhNhan;

    /**
     * Tên đầy đủ của bệnh nhân.
     */
    @Column(name = "TenBenhNhan", length = 255)
    private String tenBenhNhan;

    /**
     * Ngày tháng năm sinh.
     */
    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    /**
     * Giới tính của bệnh nhân (Nam/Nữ).
     * Thay thế cho trường Cân nặng để khớp với yêu cầu giao diện y tế.
     */
    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    /**
     * Địa chỉ thường trú hoặc tạm trú.
     */
    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    /**
     * Tên cha/mẹ hoặc người bảo hộ pháp lý (đặc biệt quan trọng với trẻ em).
     */
    @Column(name = "NguoiGiamHo", length = 255)
    private String nguoiGiamHo;

    /**
     * Số điện thoại liên lạc chính thức.
     */
    @Column(name = "SDT", length = 255)
    private String sdt;

    /**
     * Quan hệ 1-1 nối với tài khoản định danh trong hệ thống.
     */
    @OneToOne
    @JoinColumn(name = "MaTaiKhoan", referencedColumnName = "MaTaiKhoan")
    private TaiKhoan taiKhoan;

}
