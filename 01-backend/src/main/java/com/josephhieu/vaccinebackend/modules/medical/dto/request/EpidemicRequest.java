package com.josephhieu.vaccinebackend.modules.medical.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpidemicRequest {

    @NotBlank(message = "Tên dịch bệnh không được để trống")
    private String tenDichBenh;

    private String duongLayNhiem;

    private String tacHaiSucKhoe;

    @Min(value = 0, message = "Số người nhiễm không thể âm")
    private Integer soNguoiBiNhiem;

    @NotBlank(message = "Địa chỉ xảy ra dịch bệnh là bắt buộc")
    private String diaChi;

    private String ghiChu;

    @NotNull(message = "Thời điểm khảo sát không được để trống")
    private LocalDate thoiDiemKhaoSat;
}
