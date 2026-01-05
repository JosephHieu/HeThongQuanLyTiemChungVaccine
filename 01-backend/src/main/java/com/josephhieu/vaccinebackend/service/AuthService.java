package com.josephhieu.vaccinebackend.service;

import com.josephhieu.vaccinebackend.dto.request.RegisterRequest;
import com.josephhieu.vaccinebackend.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.entity.ChiTietPhanQuyen;
import com.josephhieu.vaccinebackend.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.entity.id.ChiTietPhanQuyenId;
import com.josephhieu.vaccinebackend.exception.AppException;
import com.josephhieu.vaccinebackend.exception.ErrorCode;
import com.josephhieu.vaccinebackend.repository.ChiTietPhanQuyenRepository;
import com.josephhieu.vaccinebackend.repository.PhanQuyenRepository;
import com.josephhieu.vaccinebackend.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lớp xử lý nghiệp vụ liên quan đến xác thực và quản lý tài khoản. [cite: 60]
 */
@Service
@RequiredArgsConstructor // Tự động tạo constructor injection cho các final field
public class AuthService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final PhanQuyenRepository phanQuyenRepository;
    private final ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Thực hiện đăng ký tài khoản người dùng và tự động gán quyền mặc định. [cite: 60, 96]
     * @param request Dữ liệu đăng ký từ người dùng.
     * @return UserResponse Thông tin tài khoản đã tạo (không bao gồm mật khẩu).
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {

        // Kiểm tra logic: Tên đăng nhập không được để trống
        if (request.getTenDangNhap() == null || request.getTenDangNhap().isBlank()) {
            throw new AppException(ErrorCode.MISSING_INFO);
        }

        // Kiểm tra sự tồn tại của tài khoản
        if (taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // 1. // 1. Khởi tạo Entity TaiKhoan bằng Builder
        TaiKhoan user = TaiKhoan.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .cmnd(request.getCmnd())
                .email(request.getEmail())
                .build();

        taiKhoanRepository.save(user);

        // 2. Gán quyền mặc định "Normal User Account" [cite: 60]
        PhanQuyen role = phanQuyenRepository.findByTenQuyen("Normal User Account")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        // 3. Tạo bảng trung gian phân quyền
        ChiTietPhanQuyen chiTiet = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(role.getMaQuyen(), user.getMaTaiKhoan()))
                .phanQuyen(role)
                .taiKhoan(user)
                .build();

        chiTietPhanQuyenRepository.save(chiTiet);

        // 4. Trả về Response bằng Builder
        return UserResponse.builder()
                .maTaiKhoan(user.getMaTaiKhoan())
                .tenDangNhap(user.getTenDangNhap())
                .hoTen(user.getHoTen())
                .email(user.getEmail())
                .build();
    }

}
