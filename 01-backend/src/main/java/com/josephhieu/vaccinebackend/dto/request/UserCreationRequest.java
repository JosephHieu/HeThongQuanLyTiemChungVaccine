package com.josephhieu.vaccinebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Vui lòng nhập đầy đủ thông tin.") // Thông báo lỗi
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tên đăng nhập không hợp lệ.")
    private String tenDangNhap;

    /** Mật khẩu khởi tạo. */
    @NotBlank(message = "MISSING_INFO")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên.")
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
    @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CMND không đúng định dạng.")
    private String cmnd;

    /** Nơi ở hiện tại của nhân viên. */
    @NotBlank(message = "MISSING_INFO")
    private String noiO;

    /** Thông tin mô tả/Ghi chú về nhân viên (Description). */
    private String moTa;
}
