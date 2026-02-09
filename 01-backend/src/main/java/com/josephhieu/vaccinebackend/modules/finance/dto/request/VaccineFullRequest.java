package com.josephhieu.vaccinebackend.modules.finance.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineFullRequest {

    @NotBlank(message = "Tên vắc-xin không được để trống")
    private String tenVacXin;

    @NotNull(message = "Vui lòng chọn loại vắc-xin")
    private UUID maLoaiVacXin;

    @Future(message = "Hạn sử dụng phải là một ngày trong tương lai")
    private LocalDate hanSuDung;

    @NotBlank(message = "Hàm lượng không được để trống")
    private String hamLuong;

    @NotBlank(message = "Thông tin phòng ngừa bệnh không được để trống")
    private String phongNguaBenh;

    @NotBlank(message = "Độ tuổi tiêm chủng không được để trống")
    private String doTuoiTiemChung;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", message = "Đơn giá không được nhỏ hơn 0")
    private BigDecimal donGia;

    @NotBlank(message = "Điều kiện bảo quản không được để trống")
    private String dieuKienBaoQuan;
}