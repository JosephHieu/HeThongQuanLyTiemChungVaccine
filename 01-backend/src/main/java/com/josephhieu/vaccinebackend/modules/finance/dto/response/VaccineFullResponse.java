package com.josephhieu.vaccinebackend.modules.finance.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineFullResponse {
    private UUID maVacXin;
    private String tenVacXin;

    // Thông tin loại vắc-xin
    private UUID maLoaiVacXin;
    private String tenLoaiVacXin;

    // Chi tiết y tế
    private LocalDate hanSuDung;
    private String hamLuong;
    private String phongNguaBenh;
    private String doTuoiTiemChung;
    private String dieuKienBaoQuan;

    // Thông tin tài chính
    private BigDecimal donGia;

    // Audit info (Dành cho quản lý)
    private LocalDateTime ngayCapNhat;
    private String nguoiCapNhat;
}