package com.josephhieu.vaccinebackend.modules.finance.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTransactionResponse {
    private LocalDateTime ngay;
    private String maHoaDon;
    private String maVacXin;
    private String tenVacXin;
    private Integer soLuong;
    private String tenKhachHang;
    private BigDecimal gia;
    private String trangThai;
    private String phuongThucThanhToan;
}