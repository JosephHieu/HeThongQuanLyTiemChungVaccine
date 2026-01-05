package com.josephhieu.vaccinebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Lớp cấu hình bảo mật cho hệ thống quản lý tiêm chủng.
 * Chứa các định nghĩa về mã hóa mật khẩu và phân quyền truy cập.
 * * @author Joseph Hieu
 * @version 1.0
 */

@Configuration
public class SecurityConfig {

    /**
     * Khởi tạo Bean PasswordEncoder sử dụng thuật toán BCrypt.
     * Thuật toán này giúp mã hóa mật khẩu người dùng trước khi lưu vào bảng TAIKHOAN.
     * Đáp ứng yêu cầu phi chức năng về bảo mật dữ liệu (7.3) trong SRS.
     *
     * @return PasswordEncoder đối tượng thực hiện mã hóa mạnh mẽ.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
