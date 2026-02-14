package com.josephhieu.vaccinebackend.modules.inventory.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "VACXIN")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VacXin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaVacXin", length = 36, nullable = false)
    private UUID maVacXin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoaiVacXin")
    private LoaiVacXin loaiVacXin;

    @Column(name = "TenVacXin", length = 255)
    private String tenVacXin;

    @Column(name = "HanSuDung")
    private LocalDate hanSuDung;

    @Column(name = "HamLuong", length = 255)
    private String hamLuong;

    @Column(name = "PhongNguaBenh", length = 255)
    private String phongNguaBenh;

    @Column(name = "DoTuoiTiemChung")
    private String doTuoiTiemChung;

    @Column(name = "DonGia", precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "DieuKienBaoQuan", length = 255)
    private String dieuKienBaoQuan;
}
