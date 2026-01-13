package com.josephhieu.vaccinebackend.service;

import com.josephhieu.vaccinebackend.dto.request.LoginRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lớp xử lý nghiệp vụ liên quan đến xác thực và quản lý tài khoản.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final PhanQuyenRepository phanQuyenRepository;
    private final ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Thực hiện đăng ký tài khoản người dùng và tự động gán quyền mặc định.
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

        // 1. Khởi tạo Entity TaiKhoan bằng Builder (Bổ sung đủ các trường từ Schema)
        TaiKhoan user = TaiKhoan.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau())) // Mã hóa bảo mật
                .hoTen(request.getHoTen())
                .cmnd(request.getCmnd())
                .noiO(request.getNoiO())
                .moTa(request.getMoTa())
                .email(request.getEmail())
                .build();

        user = taiKhoanRepository.save(user);

        // 2. Gán quyền mặc định "Normal User Account"
        PhanQuyen role = phanQuyenRepository.findByTenQuyen("Normal User Account")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        // 3. Tạo bảng trung gian phân quyền CHITIETPHANQUYEN
        ChiTietPhanQuyen chiTiet = ChiTietPhanQuyen.builder()
                .id(new ChiTietPhanQuyenId(role.getMaQuyen(), user.getMaTaiKhoan()))
                .phanQuyen(role)
                .taiKhoan(user)
                .build();

        chiTietPhanQuyenRepository.save(chiTiet);

        // 4. Trả về Response đầy đủ thông tin để Frontend sử dụng (Mapping thủ công hoặc dùng Mapper)
        return UserResponse.builder()
                .maTaiKhoan(user.getMaTaiKhoan())
                .tenDangNhap(user.getTenDangNhap())
                .hoTen(user.getHoTen())
                .cmnd(user.getCmnd())
                .noiO(user.getNoiO())
                .moTa(user.getMoTa())
                .email(user.getEmail())
                .roles(Collections.singleton(role.getTenQuyen())) // Trả về quyền vừa gán
                .build();
    }

    /**
     * Xử lý nghiệp vụ đăng nhập.
     * @throws AppException nếu sai thông tin hoặc tài khoản không tồn tại.
     */
    public UserResponse login(LoginRequest request) {

        // 1. Tìm tài khoản trong Database
        TaiKhoan user = taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        // 2. Kiểm tra mật khẩu đã mã hóa
        if (!passwordEncoder.matches(request.getMatKhau(), user.getMatKhau())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 3. Lấy danh sách quyền hạn để trả về
        Set<String> roles = user.getChiTietPhanQuyens().stream()
                .map(ct -> ct.getPhanQuyen().getTenQuyen())
                .collect(Collectors.toSet());

        // 4. Trả về DTO chuẩn hóa
        return UserResponse.builder()
                .maTaiKhoan(user.getMaTaiKhoan())
                .tenDangNhap(user.getTenDangNhap())
                .hoTen(user.getHoTen())
                .cmnd(user.getCmnd())
                .noiO(user.getNoiO())
                .moTa(user.getMoTa())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}