package com.josephhieu.vaccinebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "LUOTTUVAN")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LuotTuVan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaLuotTuVan", length = 36, nullable = false)
    private UUID maLuotTuVan;

    @Column(name = "CauHoi", columnDefinition = "TEXT")
    private String cauHoi;

    @Column(name = "TraLoi", columnDefinition = "TEXT")
    private String traLoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVien")
    private BenhNhan benhNhan;

    @Column(name = "CauHoiThuongGap")
    private Boolean cauHoiThuongGap;
}
