package com.josephhieu.vaccinebackend.modules.medical.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePatientRequest {

    @NotBlank(message = "Tên bệnh nhân không được để trống")
    private String tenBenhNhan;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate ngaySinh;

    private String gioiTinh;
    private String diaChi;
    private String nguoiGiamHo;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String sdt;
}
