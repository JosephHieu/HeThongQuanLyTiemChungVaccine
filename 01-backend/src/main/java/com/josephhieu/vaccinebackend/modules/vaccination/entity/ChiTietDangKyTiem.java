package com.josephhieu.vaccinebackend.modules.vaccination.entity;

import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "CHITIET_DK_TIEM")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDangKyTiem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaChiTietDKTiem", length = 36, nullable = false)
    private UUID maChiTietDKTiem;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "MaBenhNhan")
    private BenhNhan benhNhan;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "MaLo")
    private LoVacXin loVacXin;

    @Column(name = "ThoiGianCanTiem")
    private LocalDate thoiGianCanTiem;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "MaLichTiem")
    private LichTiemChung lichTiemChung;

    @Column(name = "GhiChu", columnDefinition = "TEXT")
    private String ghiChu;
}
