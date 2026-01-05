package com.josephhieu.vaccinebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String email;
}
