package com.josephhieu.vaccinebackend.modules.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineExportRequest {

    @NotNull(message = "MISSING_INFO")
    private UUID maLo;

    @NotNull(message = "INVALID_QUANTITY")
    @Min(value = 1, message = "Số lượng xuất tối thiểu là 1")
    private Integer soLuongXuat;

    @NotBlank(message = "MISSING_INFO")
    private String noiNhan; // Nơi nhận vắc-xin

    private String ghiChu;

    private UUID maNhanVien; // ID nhân viên thực hiện xuất kho
}
