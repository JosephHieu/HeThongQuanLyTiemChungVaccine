package com.josephhieu.vaccinebackend.modules.inventory.entity;

import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "LOVACXIN")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoVacXin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaLo", length = 36, nullable = false)
    private UUID maLo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaVacXin")
    private VacXin vacXin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhaCungCap")
    private NhaCungCap nhaCungCap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHoaDon")
    private HoaDon hoaDon;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "TinhTrang", length = 255)
    private String tinhTrang;

    @Column(name = "NgayNhan")
    private LocalDate ngayNhan;

    @Column(name = "NuocSanXuat", length = 255)
    private String nuocSanXuat;

    @Column(name = "GiayPhep", length = 255)
    private String giayPhep;

    @Column(name = "GhiChu", columnDefinition = "TEXT")
    private String ghiChu;
}
