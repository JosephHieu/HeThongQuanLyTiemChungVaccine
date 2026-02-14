package com.josephhieu.vaccinebackend.modules.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierTransactionResponse {

    private LocalDateTime ngayTao;
    private String maHoaDon;
    private String tenNhaCungCap;
    private String phuongThucThanhToan;
    private String trangThai; // "Đã nhập kho", "Đang giao", "Quá hạn"
    private BigDecimal tongTien;
    private Integer rawTrangThai; // Để xử lý logic ẩn/hiện nút trên UI (0, 1, 2)
}
