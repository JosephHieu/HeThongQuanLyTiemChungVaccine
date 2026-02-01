package com.josephhieu.vaccinebackend.modules.medical.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponse {

    // Thông tin hành chính (Từ BenhNhan)
    private UUID id;
    private String hoTen;
    private String gioiTinh;
    private Integer tuoi;
    private String dienThoai;
    private String diaChi;
    private String nguoiGiamHo;

    // Mũi tiêm gần nhất (Từ HoSoBenhAn -> ChiTietDangKyTiem -> LoVacXin -> Vacxin)
    private String vacxinDaTiem;
    private String maLo;
    private String thoiGianTiemTruoc;
    private String phanUng;

    // Chỉ định tiếp theo (Từ ChiTietDangKyTiem chưa có hồ sơ)
    private String vacxinCanTiem;
    private String thoiGianTiemTiepTheo;
}
