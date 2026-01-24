package com.josephhieu.vaccinebackend.modules.inventory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineImportRequest {

    // Thông tin Vắc-xin (Tạo mới hoặc liên kết)
    private String tenVacXin;
    private UUID maLoaiVacXin;
    private String hamLuong;
    private String phongNguaBenh;
    private String doTuoiTiemChung;
    private BigDecimal donGia;
    private String dieuKienBaoQuan;
    private LocalDate hanSuDung;

    // Thông tin bảng LOVACXIN
    private String maLo; // Số lô nhập từ màn hình
    private UUID maNhaCungCap; // UUID
    private Integer soLuong;
    private LocalDate ngayNhan;
    private String nuocSanXuat;
    private String giayPhep;
    private String ghiChu;
}
