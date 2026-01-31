package com.josephhieu.vaccinebackend.config;

import com.josephhieu.vaccinebackend.modules.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        .requestMatchers("/api/auth/**").permitAll()

                        // Quyền tối cao của Admin
                        .requestMatchers("/api/admin/**", "/api/roles/**", "/api/users/**").hasAuthority("Administrator")

                        // Quyền Quản lý kho
                        .requestMatchers("/api/inventory/**").hasAnyAuthority("Administrator", "Quản lý kho")

                        // Quyền Nhân viên y tế
                        .requestMatchers("/api/medical/**").hasAnyAuthority("Administrator", "Nhân viên y tế")

                        // Quyền Tài chính
                        .requestMatchers("/api/finance/**").hasAnyAuthority("Administrator", "Tài chính")

                        // Quyền Hỗ trợ khách hàng
                        .requestMatchers("/api/support/**").hasAnyAuthority("Administrator", "Hỗ trợ khách hàng")

                        // Quyền cho người dùng (Normal User Account)
                        .requestMatchers("/api/patients/me/**", "/api/vaccinations/**").hasAuthority("Normal User Account")                        .anyRequest().authenticated()
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
