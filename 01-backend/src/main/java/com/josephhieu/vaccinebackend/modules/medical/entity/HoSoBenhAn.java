package com.josephhieu.vaccinebackend.modules.medical.entity;

import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.ChiTietDangKyTiem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "HOSOBENHAN")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoSoBenhAn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaHoSoBenhAn", length = 36, nullable = false)
    private UUID maHoSoBenhAn;

    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "MaChiTietDKTiem")
    private ChiTietDangKyTiem chiTietDangKyTiem;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "MaHoaDon")
    private HoaDon hoaDon;

    @Column(name = "PhanUngSauTiem", columnDefinition = "TEXT")
    private String phanUngSauTiem;

    @Column(name = "ThoiGianTacDung", length = 255)
    private String thoiGianTacDung;

    @Column(name = "ThoiGianTiem")
    private LocalDateTime thoiGianTiem;
}
