package com.josephhieu.vaccinebackend.modules.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffSummaryResponse {

    private UUID maNhanVien; // Lấy từ bảng NHANVIEN
    private String tenNhanVien; // Để hiển thị lên UI
}
