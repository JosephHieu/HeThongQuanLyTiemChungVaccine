package com.josephhieu.vaccinebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
}
