package com.josephhieu.vaccinebackend.modules.identity.controller;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.StaffSummaryResponse;
import com.josephhieu.vaccinebackend.modules.identity.dto.response.UserResponse;
import com.josephhieu.vaccinebackend.modules.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller quản lý tài khoản người dùng và định danh hệ thống.
 * <p>
 * Chịu trách nhiệm thực hiện các thao tác quản trị nhân sự bao gồm:
 * Khởi tạo tài khoản nhân viên, phân quyền (Role assignment), quản lý trạng thái hoạt động
 * và cung cấp danh sách nhân sự chuyên môn cho các phân hệ y tế khác.
 * </p>
 *
 * @author Joseph Hieu
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Khởi tạo tài khoản nhân viên hoặc người dùng mới trên hệ thống.
     * <p>
     * Quy trình bao gồm việc kiểm tra tính duy nhất của danh tính, mã hóa mật khẩu
     * và thiết lập các vai trò mặc định dựa trên yêu cầu.
     * </p>
     *
     * @param request Thông tin chi tiết tài khoản cần khởi tạo.
     * @return {@link ResponseEntity} với mã 201 (Created) xác nhận tài khoản đã được thiết lập.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('Administrator')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Khởi tạo tài khoản người dùng mới: {}", request.getTenDangNhap());
        UserResponse result = userService.createNewUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Tạo tài khoản người dùng thành công."));
    }

    /**
     * Truy xuất danh sách toàn bộ tài khoản người dùng với khả năng lọc và phân trang.
     * <p>
     * Hỗ trợ tìm kiếm linh hoạt theo tên tài khoản hoặc lọc theo nhóm quyền hạn (Role).
     * </p>
     *
     * @param page Chỉ số trang hiện tại (bắt đầu từ 1).
     * @param size Số lượng bản ghi trên mỗi trang.
     * @param search Từ khóa tìm kiếm tên người dùng hoặc email.
     * @param maQuyen Mã quyền hạn cần lọc.
     * @return {@link ResponseEntity} chứa trang dữ liệu người dùng kết quả.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('Administrator')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String maQuyen
    ) {
        log.info("Truy xuất danh sách người dùng phân trang - Trang: {}, Từ khóa: {}", page, search);
        PageResponse<UserResponse> result = userService.getAllUsers(page, size, search, maQuyen);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Cập nhật thông tin chi tiết của một tài khoản người dùng hiện có.
     *
     * @param id Mã định danh duy nhất (UUID) của người dùng cần cập nhật.
     * @param request Dữ liệu thông tin tài khoản mới.
     * @return {@link ResponseEntity} chứa thông tin người dùng sau khi chỉnh sửa.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Administrator')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID id, @RequestBody @Valid UserCreationRequest request) {
        log.info("Cập nhật thông tin tài khoản ID: {}", id);
        UserResponse result = userService.updateUser(id, request);

        return ResponseEntity.ok(ApiResponse.success(result, "Cập nhật thông tin tài khoản thành công."));
    }

    /**
     * Truy xuất danh sách rút gọn các nhân viên y tế (Bác sĩ/Y tá) hiện đang hoạt động.
     * <p>
     * API này được thiết kế để cung cấp dữ liệu cho các phân hệ điều phối tiêm chủng
     * khi cần phân công cán bộ phụ trách các ca tiêm.
     * </p>
     *
     * @return {@link ResponseEntity} danh sách nhân sự y tế phù hợp.
     */
    @GetMapping("/medical-staffs")
    @PreAuthorize("hasAnyAuthority('Administrator', 'Nhân viên y tế')")
    public ResponseEntity<ApiResponse<List<StaffSummaryResponse>>> getMedicalStaffs() {
        log.info("Truy vấn danh sách nhân viên y tế phục vụ công tác điều phối lịch trực.");
        List<StaffSummaryResponse> result = userService.getStaffsByRole("Nhân viên y tế");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Thay đổi trạng thái hoạt động (Khóa/Mở khóa) của một tài khoản người dùng.
     * <p>
     * Hành động khóa tài khoản sẽ ngay lập tức vô hiệu hóa khả năng đăng nhập của người dùng
     * nhưng vẫn giữ lại lịch sử dữ liệu liên quan.
     * </p>
     *
     * @param id Mã định danh người dùng cần thay đổi trạng thái.
     * @return {@link ResponseEntity} thông báo kết quả thao tác.
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyAuthority('Administrator')")
    public ResponseEntity<ApiResponse<String>> toggleStatus(@PathVariable UUID id) {
        log.warn("Thay đổi trạng thái hoạt động của tài khoản ID: {}", id);
        userService.toggleLock(id);

        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công"));
    }
}