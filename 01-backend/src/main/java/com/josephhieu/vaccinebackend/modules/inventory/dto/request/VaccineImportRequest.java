package com.josephhieu.vaccinebackend.modules.inventory.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineImportRequest {

    // Thông tin Vắc-xin
    @NotBlank(message = "MISSING_INFO")
    private String tenVacXin;

    @NotNull(message = "MISSING_INFO")
    private UUID maLoaiVacXin;

    @NotBlank(message = "MISSING_INFO")
    private String soLo;

    private String hamLuong;
    private String phongNguaBenh;
    private String doTuoiTiemChung;

    @NotNull(message = "MISSING_INFO")
    @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải lớn hơn 0")
    private BigDecimal donGia;

    private String dieuKienBaoQuan;

    @NotNull(message = "MISSING_INFO")
    @Future(message = "Hạn sử dụng phải là một ngày trong tương lai")
    private LocalDate hanSuDung;

    // Thông tin bảng LOVACXIN

    @NotNull(message = "Nhà cung cấp không được để trống")
    private UUID maNhaCungCap; // UUID

    @NotNull(message = "INVALID_IMPORT_QUANTITY")
    @Min(value = 1, message = "Số lượng nhập ít nhất là 1 liều")
    private Integer soLuong;

    @NotNull(message = "Ngày nhận không được để trống")
    @PastOrPresent(message = "Ngày nhận không được vượt quá thời gian hiện tại")
    private LocalDate ngayNhan;

    private String nuocSanXuat;
    private String giayPhep;
    private String ghiChu;
}
