package com.josephhieu.vaccinebackend.modules.vaccination.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * DTO phụ để chứa thông tin rút gọn của nhân viên y tế.
 */
@Data
@Builder
public class StaffSummaryResponse {

    private UUID maNhanVien;
    private String tenNhanVien;
}
