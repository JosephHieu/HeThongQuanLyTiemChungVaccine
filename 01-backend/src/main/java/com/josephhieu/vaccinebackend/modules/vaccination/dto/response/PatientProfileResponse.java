package com.josephhieu.vaccinebackend.modules.vaccination.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileResponse {

    private UUID maBenhNhan; // <--- Thêm dòng này để hết lỗi Builder
    private String tenBenhNhan;
    private String ngaySinh;
    private String gioiTinh;
    private String sdt;
    private String diaChi;
    private String nguoiGiamHo;
    private String email;
    private String cmnd;
    private List<VaccinationHistoryResponse> lichSuTiem;
}