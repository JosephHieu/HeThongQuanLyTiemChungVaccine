package com.josephhieu.vaccinebackend.modules.vaccination.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationHistoryResponse {

    private String ngayTiem;
    private String diaDiem;
    private String tenVacXin;
    private String loaiVacXin;
    private String nhanVienThucHien;
    private String phanUngSauTiem;
    private String ghiChu;
}
