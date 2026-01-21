package com.josephhieu.vaccinebackend.modules.finance.entity;

import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "PHANHOI")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhanHoi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaPhanHoi", length = 36, nullable = false)
    private UUID maPhanHoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoaiPhanHoi")
    private LoaiPhanHoi loaiPhanHoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaBenhNhan")
    private BenhNhan benhNhan;

    @Column(name = "TenNhanVienPhuTrach",length = 255)
    private String tenNhanVienPhuTrach;

    @Column(name = "TenVacXin", length = 255)
    private String tenVacXin;

    @Column(name = "NoiDung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "ThoiGianTiem")
    private LocalDate thoiGianTiem;

    @Column(name = "DiaDiemTiem", length = 255)
    private String diaDiemTiem;
}
