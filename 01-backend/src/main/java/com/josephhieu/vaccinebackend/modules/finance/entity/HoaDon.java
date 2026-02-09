package com.josephhieu.vaccinebackend.modules.finance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "HOADON")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaHoaDon", length = 36, nullable = false)
    private UUID maHoaDon;

    @Column(name = "TongTien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @Column(name = "TrangThai")
    private Integer trangThai; // 0: Chờ thanh toán, 1: Đã thanh toán, 2: Đã hủy

    @Column(name = "PhuongThucThanhToan")
    private String phuongThucThanhToan; // "Tiền mặt", "Chuyển khoản", "Thẻ"

    @Column(name = "LoaiHoaDon")
    private String loaiHoaDon; // "XUAT" (Bán cho khách), "NHAP" (Nhập từ NCC)
}
