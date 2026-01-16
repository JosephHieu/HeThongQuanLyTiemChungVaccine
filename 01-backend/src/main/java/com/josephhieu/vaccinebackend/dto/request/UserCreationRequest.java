package com.josephhieu.vaccinebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng vận chuyển dữ liệu khi tạo tài khoản mới.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {

    /** Tên đăng nhập của nhân viên. */
    @NotBlank(message = "Vui lòng nhập đầy đủ thông tin.") // Thông báo lỗi theo SRS
    private String tenDangNhap;

    /** Mật khẩu khởi tạo. */
    @NotBlank(message = "MISSING_INFO")
    private String matKhau;

    /** * Mã quyền hạn (UUID).
     * Dùng để ánh xạ vào bảng PHANQUYEN (Administrator, Quản lý kho, Tài chính...).
     */
    @NotBlank(message = "MISSING_INFO")
    private String maQuyen;

    /** Họ tên nhân viên. */
    @NotBlank(message = "MISSING_INFO")
    private String hoTen;

    /** Số chứng minh nhân dân/CCCD. */
    @NotBlank(message = "MISSING_INFO")
    private String cmnd;

    /** Nơi ở hiện tại của nhân viên. */
    @NotBlank(message = "MISSING_INFO")
    private String noiO;

    /** Thông tin mô tả/Ghi chú về nhân viên (Description). */
    private String moTa;
}
