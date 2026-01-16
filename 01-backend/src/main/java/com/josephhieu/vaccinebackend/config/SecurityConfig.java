package com.josephhieu.vaccinebackend.config;

import com.josephhieu.vaccinebackend.security.CustomUserDetailsService;
import com.josephhieu.vaccinebackend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Lớp cấu hình bảo mật cho hệ thống quản lý tiêm chủng.
 * Chứa các định nghĩa về mã hóa mật khẩu và phân quyền truy cập.
 * * @author Joseph Hieu
 * @version 1.0
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Cấu hình AuthenticationManager để sử dụng CustomUserDetailsService và BCrypt.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Bây giờ IDE sẽ nhận diện được phương thức này
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()) // Đăng ký bộ xác thực
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("api/admin/**").hasAuthority("Administrator")
                        .requestMatchers(HttpMethod.GET, "/api/user/**").hasAuthority("ROLE_Administrator")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("ROLE_Administrator")
                        .requestMatchers("/api/kho/**").hasAnyAuthority("Administrator", "Quản lý kho")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

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
