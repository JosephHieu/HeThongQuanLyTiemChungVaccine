package com.josephhieu.vaccinebackend.security;

import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Lớp chuyển đổi từ thực thể TaiKhoan sang đối tượng UserDetails mà Spring Security có thể hiểu.
 * Giúp hệ thống kiểm tra mật khẩu và phân quyền dựa trên dữ liệu thực tế.
 */
@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private TaiKhoan taiKhoan;

    /**
     * Lấy danh sách các quyền (Authorities) của người dùng từ bảng PHANQUYEN.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return taiKhoan.getChiTietPhanQuyens().stream()
                .map(ct -> new SimpleGrantedAuthority(ct.getPhanQuyen().getTenQuyen()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return taiKhoan.getMatKhau();
    }

    @Override
    public String getUsername() {
        return taiKhoan.getTenDangNhap();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return taiKhoan.isTrangThai(); }
}
