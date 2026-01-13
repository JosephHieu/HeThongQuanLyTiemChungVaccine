package com.josephhieu.vaccinebackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

/**
 * Đối tượng yêu cầu đăng ký tài khoản người dùng mới.
 * Chứa thông tin tổng hợp cho cả bảng TAIKHOAN và BENHNHAN.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    // --- Thông tin tài khoản (Ánh xạ bảng TAIKHOAN) ---

    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;

    @Email(message = "Email không đúng định dạng")
    private String email;

    /** Thông tin mô tả người dùng (Description). */
    private String moTa;

    // --- Thông tin cá nhân (Ánh xạ bảng BENHNHAN & TAIKHOAN) ---

    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotBlank(message = "Số CMND không được để trống")
    private String cmnd;

    private String sdt;

    /** * Địa chỉ nơi ở. Trong bảng TAIKHOAN là NoiO, bảng BENHNHAN là DiaChi.
     * Dùng chung một trường để đồng bộ.
     */
    @NotBlank(message = "Nơi ở không được để trống")
    private String noiO;

    private String gioiTinh;

    private LocalDate ngaySinh;

    private String nguoiGiamHo;
}