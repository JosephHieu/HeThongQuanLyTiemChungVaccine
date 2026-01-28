package com.josephhieu.vaccinebackend.modules.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PHIEUXUAT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaPhieuXuat", length = 36, nullable = false)
    private UUID maPhieuXuat;

    @Column(name = "SoPhieuXuat", nullable = false, unique = true)
    private String soPhieuXuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLo", nullable = false)
    private LoVacXin loVacXin;

    @Column(name = "MaNhanVien", length = 36)
    private UUID maNhanVien;

    @CreationTimestamp
    @Column(name = "NgayXuat", updatable = false)
    private LocalDateTime ngayXuat;

    @Column(name = "SoLuongXuat", nullable = false)
    private Integer soLuongXuat;

    @Column(name = "NoiNhan", length = 255)
    private String noiNhan;

    @Column(name = "GhiChu", columnDefinition = "TEXT")
    private String ghiChu;
}
