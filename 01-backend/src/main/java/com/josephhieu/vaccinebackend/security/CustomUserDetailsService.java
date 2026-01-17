package com.josephhieu.vaccinebackend.security;

import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.exception.AppException;
import com.josephhieu.vaccinebackend.exception.ErrorCode;
import com.josephhieu.vaccinebackend.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp cung cấp dịch vụ tìm kiếm thông tin tài khoản từ cơ sở dữ liệu.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;

    /**
     * Tìm kiếm tài khoản dựa trên tên đăng nhập.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return new CustomUserDetails(taiKhoan);
    }
}
