package com.josephhieu.vaccinebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Đối tượng chứa thông tin yêu cầu đăng nhập.
 * Khớp với các trường Username và Password trên giao diện Login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;
}