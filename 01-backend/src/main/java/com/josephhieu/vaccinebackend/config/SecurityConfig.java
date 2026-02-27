package com.josephhieu.vaccinebackend.config;

import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Lớp cấu hình bảo mật cho hệ thống quản lý tiêm chủng.
 * Chứa các định nghĩa về mã hóa mật khẩu và phân quyền truy cập.
 * * @author Joseph Hieu
 * @version 1.0
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()) // Đăng ký bộ xác thực
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập công khai các API xác thực
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // QUYỀN ADMIN: Quản lý người dùng, vai trò
                        .requestMatchers("/api/v1/admin/**", "/api/v1/roles/**", "/api/v1/users/**").hasAuthority("Administrator")

                        // QUYỀN KHO: Quản lý vắc-xin, lô hàng
                        .requestMatchers("/api/v1/inventory/**").hasAnyAuthority("Administrator", "Quản lý kho")

                        // QUYỀN Y TẾ: Hồ sơ bệnh án, ĐIỀU PHỐI LỊCH TIÊM (Đã bổ sung vaccination/**)
                        .requestMatchers("/api/v1/medical/feedback/**").hasAnyAuthority("Administrator", "Nhân viên y tế", "Normal User Account")
                        .requestMatchers("/api/v1/medical/my-profile", "/api/v1/medical/my-history").hasAnyAuthority("Administrator", "Nhân viên y tế", "Normal User Account")
                        .requestMatchers("/api/v1/medical/high-level-feedback/**").hasAnyAuthority("Administrator", "Nhân viên y tế", "Normal User Account")
                        .requestMatchers("/api/v1/medical/epidemics/**").hasAnyAuthority("Administrator", "Nhân viên y tế", "Normal User Account")
                        .requestMatchers("/api/v1/vaccination/schedules/opening").hasAnyAuthority("Administrator", "Nhân viên y tế", "Normal User Account")
                        .requestMatchers("/api/v1/medical/**", "/api/v1/vaccination/**").hasAnyAuthority("Administrator", "Nhân viên y tế")

                        // QUYỀN TÀI CHÍNH: (Đã thêm v1)
                        .requestMatchers("/api/v1/finance/**").hasAnyAuthority("Administrator", "Tài chính")

                        // QUYỀN HỖ TRỢ: Nhắc lịch, phản hồi
                        .requestMatchers("/api/v1/support/**").hasAnyAuthority("Administrator", "Hỗ trợ khách hàng")

                        // QUYỀN BỆNH NHÂN: (Đã thêm v1)
                        .requestMatchers("/api/v1/patients/me/**", "/api/v1/vaccinations/**").hasAuthority("Normal User Account")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

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

    // Định nghĩa quy tắc CORS cho frontend vite (port 5173)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // URL của Frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
