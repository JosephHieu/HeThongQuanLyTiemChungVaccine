package com.josephhieu.vaccinebackend.service.impl;

import com.josephhieu.vaccinebackend.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.entity.ChiTietPhanQuyen;
import com.josephhieu.vaccinebackend.entity.NhanVien;
import com.josephhieu.vaccinebackend.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.entity.id.ChiTietPhanQuyenId;
import com.josephhieu.vaccinebackend.exception.AppException;
import com.josephhieu.vaccinebackend.exception.ErrorCode;
import com.josephhieu.vaccinebackend.repository.ChiTietPhanQuyenRepository;
import com.josephhieu.vaccinebackend.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.repository.PhanQuyenRepository;
import com.josephhieu.vaccinebackend.repository.TaiKhoanRepository;
import com.josephhieu.vaccinebackend.service.UserService;
import lombok.RequiredArgsConstructor;

// SỬA LẠI CÁC IMPORT NÀY
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final TaiKhoanRepository taiKhoanRepository;
    private final PhanQuyenRepository phanQuyenRepository;
    private final ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Chuyển đổi từ Entity sang DTO để bảo mật và tránh vòng lặp JSON.
     */
    private UserResponse mapToUserResponse(TaiKhoan taiKhoan) {
        return UserResponse.builder()
                .maTaiKhoan(taiKhoan.getMaTaiKhoan())
                .tenDangNhap(taiKhoan.getTenDangNhap())
                .hoTen(taiKhoan.getHoTen())
                .cmnd(taiKhoan.getCmnd())
                .noiO(taiKhoan.getNoiO())
                .moTa(taiKhoan.getMoTa())
                .email(taiKhoan.getEmail())
                .roles(taiKhoan.getChiTietPhanQuyens().stream()
                        .map(ct -> ct.getPhanQuyen().getTenQuyen())
                        .collect(Collectors.toSet()))
                .token(null)
                .build();
    }

    @Override
    @Transactional
    public UserResponse createNewUser(UserCreationRequest request) {
        if (taiKhoanRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        PhanQuyen role = phanQuyenRepository.findById(UUID.fromString(request.getMaQuyen()))
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        TaiKhoan taiKhoan = TaiKhoan.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .cmnd(request.getCmnd())
                .noiO(request.getNoiO())
                .moTa(request.getMoTa())
                .build();
        TaiKhoan savedAccount = taiKhoanRepository.save(taiKhoan);

        NhanVien nhanVien = NhanVien.builder()
                .tenNhanVien(request.getHoTen())
                .taiKhoan(savedAccount)
                .build();
        nhanVienRepository.save(nhanVien);

        ChiTietPhanQuyenId id = new ChiTietPhanQuyenId(role.getMaQuyen(), savedAccount.getMaTaiKhoan());
        ChiTietPhanQuyen detail = ChiTietPhanQuyen.builder()
                .id(id)
                .phanQuyen(role)
                .taiKhoan(savedAccount)
                .build();
        chiTietPhanQuyenRepository.save(detail);

        return mapToUserResponse(savedAccount);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        // Sử dụng org.springframework.data.domain.Pageable
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("tenDangNhap").ascending());

        Page<TaiKhoan> userPage = taiKhoanRepository.findAll(pageable);

        List<UserResponse> users = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .data(users)
                .build();
    }
}