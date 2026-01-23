package com.josephhieu.vaccinebackend.modules.inventory.dto.request;

import jakarta.validation.constraints.Min;
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

    @NotNull(message = "Mã lô không được để trống")
    private UUID maLo;

    @Min(value = 1, message = "Số lượng xuất tối thiểu là 1")
    private Integer soLuongXuat;
}
