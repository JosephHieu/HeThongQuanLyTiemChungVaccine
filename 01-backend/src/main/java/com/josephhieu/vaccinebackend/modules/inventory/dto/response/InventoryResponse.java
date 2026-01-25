package com.josephhieu.vaccinebackend.modules.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponse {

    private UUID maLo; // Khóa chính (Internal ID)
    private String soLo; // Mã số lô hiển thị cho người dùng (External ID)

    private String tenVacXin;
    private String tenLoaiVacXin;
    private String hamLuong;
    private String phongNguaBenh;
    private String doTuoiTiemChung;

    private Integer soLuong;
    private BigDecimal donGia; // Đơn giá vắc-xin

    private LocalDate ngayNhan;
    private LocalDate hanSuDung;

    private String tinhTrang; // "Còn" hoặc "Hết" (Có thể tính toán từ soLuong)
    private String nuocSanXuat;
    private String giayPhep;
    private String dieuKienBaoQuan;
}
