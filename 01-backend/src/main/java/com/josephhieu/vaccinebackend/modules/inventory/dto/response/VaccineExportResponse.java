package com.josephhieu.vaccinebackend.modules.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineExportResponse {

    private String soPhieuXuat;
    private UUID maPhieuXuat;
    private String tenVacXin;
    private String soLoThucTe; // Lấy từ LoVacXin.soLo
    private Integer soLuongDaXuat;
    private Integer soLuongConLaiTrongKho; // Đề Frontend cập nhật lại UI
    private LocalDateTime ngayXuat;
    private String noiNhan;
}
