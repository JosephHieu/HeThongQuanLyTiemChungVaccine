package com.josephhieu.vaccinebackend.modules.identity.service.impl;

import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.StaffSummaryResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.entity.*;
import com.josephhieu.vaccinebackend.modules.identity.entity.id.ChiTietPhanQuyenId;
import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.repository.*;
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
    private final BenhNhanRepository benhNhanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createNewUser(UserCreationRequest request) {
        // 1. Kiểm tra tồn tại tên đăng nhập
        if (taiKhoanRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // 2. Lấy thông tin Quyền
        PhanQuyen role = phanQuyenRepository.findById(UUID.fromString(request.getMaQuyen()))
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        // 3. Lưu tài khoản chính
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .tenDangNhap(request.getTenDangNhap())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .hoTen(request.getHoTen())
                .cmnd(request.getCmnd())
                .noiO(request.getNoiO())
                .moTa(request.getMoTa())
                .email(request.getEmail())
                .trangThai(true)
                .build();
        TaiKhoan savedAccount = taiKhoanRepository.save(taiKhoan);

        // 4. LOGIC RẼ NHÁNH
        if (role.getTenQuyen().equals("Normal User Account")) {
            BenhNhan benhNhan = BenhNhan.builder()
                    .taiKhoan(savedAccount)
                    .tenBenhNhan(request.getHoTen())
                    .ngaySinh(request.getNgaySinh())
                    .gioiTinh(request.getGioiTinh())
                    .diaChi(request.getNoiO())       // DiaChi nên lấy từ noiO của request
                    .sdt(request.getSdt())           // Gán đúng trường sdt
                    .nguoiGiamHo(request.getNguoiGiamHo())
                    .build();
            benhNhanRepository.save(benhNhan);
        } else {
            NhanVien nhanVien = NhanVien.builder()
                    .tenNhanVien(request.getHoTen())
                    .taiKhoan(savedAccount)
                    .build();
            nhanVienRepository.save(nhanVien);
        }

        // 5. Lưu chi tiết phân quyền
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
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(int page, int size, String search, String maQuyen) {
        // 1. Validate trang
        int validatedPage = (page < 1) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(validatedPage, size, Sort.by("tenDangNhap").ascending());

        // 2. Xử lý UUID từ String (tránh lỗi nếu maQuyen gửi lên bị rỗng)
        UUID roleUuid = (maQuyen != null && !maQuyen.isEmpty()) ? UUID.fromString(maQuyen) : null;

        // 3. Xử lý Search rỗng
        String searchKeyword = (search != null && !search.isEmpty()) ? search : null;

        // 4. Gọi Repository với bộ lọc mới
        Page<TaiKhoan> userPage = taiKhoanRepository.findAllWithFilter(searchKeyword, roleUuid, pageable);

        List<UserResponse> users = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .currentPage(validatedPage + 1)
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

        // 2. Cập nhật thông tin cơ bản tại bảng TAIKHOAN
        taiKhoan.setHoTen(request.getHoTen());
        taiKhoan.setCmnd(request.getCmnd());
        taiKhoan.setNoiO(request.getNoiO());
        taiKhoan.setMoTa(request.getMoTa());
        taiKhoan.setEmail(request.getEmail());

        // 3. Xử lý cập nhật quyền
        chiTietPhanQuyenRepository.deleteByTaiKhoan(taiKhoan);

        UUID roleId = UUID.fromString(request.getMaQuyen());
        PhanQuyen phanQuyenMoi = phanQuyenRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        ChiTietPhanQuyenId compositeId = new ChiTietPhanQuyenId(roleId, taiKhoan.getMaTaiKhoan());
        ChiTietPhanQuyen chiTietMoi = ChiTietPhanQuyen.builder()
                .id(compositeId)
                .taiKhoan(taiKhoan)
                .phanQuyen(phanQuyenMoi)
                .build();
        chiTietPhanQuyenRepository.save(chiTietMoi);

        // 4. CẬP NHẬT THÔNG TIN CHI TIẾT (Bệnh nhân hoặc Nhân viên)
        if (phanQuyenMoi.getTenQuyen().equals("Normal User Account")) {
            // Tìm hồ sơ bệnh nhân hiện có, nếu không có (do trước đó là nhân viên) thì tạo mới
            BenhNhan benhNhan = benhNhanRepository.findByTaiKhoan(taiKhoan)
                    .orElse(new BenhNhan());

            benhNhan.setTaiKhoan(taiKhoan);
            benhNhan.setTenBenhNhan(request.getHoTen());
            benhNhan.setNgaySinh(request.getNgaySinh());
            benhNhan.setGioiTinh(request.getGioiTinh());
            benhNhan.setDiaChi(request.getNoiO());
            benhNhan.setSdt(request.getSdt());
            benhNhan.setNguoiGiamHo(request.getNguoiGiamHo());

            benhNhanRepository.save(benhNhan);

            // (Tùy chọn) Xóa bản ghi bên bảng NHANVIEN nếu có để tránh rác
            nhanVienRepository.findByTaiKhoan(taiKhoan).ifPresent(nhanVienRepository::delete);
        } else {
            // Cập nhật bảng NHANVIEN
            NhanVien nhanVien = nhanVienRepository.findByTaiKhoan(taiKhoan)
                    .orElse(new NhanVien());

            nhanVien.setTaiKhoan(taiKhoan);
            nhanVien.setTenNhanVien(request.getHoTen());

            nhanVienRepository.save(nhanVien);

            // (Tùy chọn) Xóa bản ghi bên bảng BENHNHAN nếu có
            benhNhanRepository.findByTaiKhoan(taiKhoan).ifPresent(benhNhanRepository::delete);
        }

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

    @Override
    @Transactional(readOnly = true)
    public List<StaffSummaryResponse> getStaffsByRole(String roleName) {
        // 1. Gọi Repository để Join 4 bảng (NhanVien -> TaiKhoan -> ChiTietPQ -> PhanQuyen)
        // Lưu ý: Đảm bảo bạn đã thêm hàm findStaffByRoleName vào NhanVienRepository như bước trước
        List<NhanVien> staffs = nhanVienRepository.findStaffByRoleName(roleName);

        // 2. Chuyển đổi sang DTO rút gọn
        return staffs.stream()
                .map(nv -> StaffSummaryResponse.builder()
                        .maNhanVien(nv.getMaNhanVien())
                        .tenNhanVien(nv.getTenNhanVien())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * Chuyển đổi từ Entity sang DTO để bảo mật và tránh vòng lặp JSON.
     */
    private UserResponse mapToUserResponse(TaiKhoan user) {
        UserResponse response = UserResponse.builder()
                .maTaiKhoan(user.getMaTaiKhoan())
                .tenDangNhap(user.getTenDangNhap())
                .hoTen(user.getHoTen())
                .email(user.getEmail())
                .cmnd(user.getCmnd())
                .noiO(user.getNoiO())
                .moTa(user.getMoTa())
                .trangThai(user.isTrangThai())
                .roles(user.getChiTietPhanQuyens().stream()
                        .map(ct -> ct.getPhanQuyen().getTenQuyen())
                        .collect(Collectors.toSet()))
                .build();

        // NẾU LÀ BỆNH NHÂN -> LẤY THÊM THÔNG TIN Y TẾ
        benhNhanRepository.findByTaiKhoan(user).ifPresent(bn -> {
            response.setSdt(bn.getSdt());
            response.setNgaySinh(bn.getNgaySinh());
            response.setGioiTinh(bn.getGioiTinh());
            response.setNguoiGiamHo(bn.getNguoiGiamHo());
        });

        return response;
    }
}