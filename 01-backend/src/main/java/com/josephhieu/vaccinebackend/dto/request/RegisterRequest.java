package com.josephhieu.vaccinebackend.dto.request;

import lombok.Data;

/**
 * Đối tượng yêu cầu đăng ký tài khoản người dùng mới.
 */
@Data
public class RegisterRequest {

    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String cmnd;
    private String email;
}
