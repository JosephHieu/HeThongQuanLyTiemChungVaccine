package com.josephhieu.vaccinebackend.modules.medical.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationHistoryResponse {

    private String thoiGian;
    private String diaDiem;
    private String tenVacXin;
    private String loaiVacXin;
    private String lieuLuong;
    private String nguoiTiem;
    private String ketQua;
}
