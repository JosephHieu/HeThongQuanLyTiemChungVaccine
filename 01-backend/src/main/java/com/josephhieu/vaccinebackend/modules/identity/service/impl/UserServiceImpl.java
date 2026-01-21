package com.josephhieu.vaccinebackend.modules.identity.service.impl;

import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.entity.ChiTietPhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.entity.id.ChiTietPhanQuyenId;
import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.repository.ChiTietPhanQuyenRepository;
import com.josephhieu.vaccinebackend.modules.identity.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.modules.identity.repository.PhanQuyenRepository;
import com.josephhieu.vaccinebackend.modules.identity.repository.TaiKhoanRepository;
import com.josephhieu.vaccinebackend.modules.identity.service.UserService;
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
                .trangThai(taiKhoan.isTrangThai())
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

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserCreationRequest request) {

        // 1. Tìm tài khoản hiện có
        TaiKhoan taiKhoan = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Cập nhật thông tin cơ bản
        taiKhoan.setHoTen(request.getHoTen());
        taiKhoan.setCmnd(request.getCmnd());
        taiKhoan.setNoiO(request.getNoiO());
        taiKhoan.setMoTa(request.getMoTa());

        // 3. Xử lý cập nhật quyền
        // Xóa quyền cũ
        chiTietPhanQuyenRepository.deleteByTaiKhoan(taiKhoan);

        // Chuyển String sang UUID
        UUID roleId = UUID.fromString(request.getMaQuyen());
        PhanQuyen phanQuyenMoi = phanQuyenRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // Khởi tạo khóa phức hợp (EmbeddedId)
        ChiTietPhanQuyenId compositeId = new ChiTietPhanQuyenId(roleId, taiKhoan.getMaTaiKhoan());

        ChiTietPhanQuyen chiTietMoi = ChiTietPhanQuyen.builder()
                .id(compositeId) // Gán ID phức hợp
                .taiKhoan(taiKhoan)
                .phanQuyen(phanQuyenMoi)
                .build();

        chiTietPhanQuyenRepository.save(chiTietMoi);

        return mapToUserResponse(taiKhoanRepository.save(taiKhoan));
    }

    @Override
    @Transactional
    public void toggleLock(UUID id) {

        TaiKhoan taiKhoan = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        taiKhoan.setTrangThai(!taiKhoan.isTrangThai());
        taiKhoanRepository.save(taiKhoan);
    }

}