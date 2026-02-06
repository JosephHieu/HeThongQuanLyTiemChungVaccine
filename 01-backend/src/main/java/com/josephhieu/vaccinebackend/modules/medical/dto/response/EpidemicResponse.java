package com.josephhieu.vaccinebackend.modules.medical.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpidemicResponse {

    private UUID maDichBenh;

    private String thoiDiemKhaoSat;

    private String diaChi;

    private String tenDichBenh;

    private Integer soNguoiBiNhiem;

    private String duongLayNhiem;

    private String tacHaiSucKhoe;

    private String ghiChu;

    /**
     * Hiển thị: Vắc-xin phòng bệnh
     * Danh sách các vắc-xin có công dụng phòng bệnh này (lấy từ thực thể VacXin)
     */
    private List<String> vacXinGoiY;

    private String tenNhanVienKhaoSat;
}
