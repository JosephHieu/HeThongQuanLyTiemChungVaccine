package com.josephhieu.vaccinebackend.modules.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Đối tượng trả về thông tin người dùng sau khi đăng ký hoặc truy vấn thành công.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID maTaiKhoan;
    private String tenDangNhap;
    private String hoTen;
    private String cmnd;
    private String noiO;
    private String moTa;
    private String email;
    private Set<String> roles;

    private String token;

    private boolean trangThai;
}
