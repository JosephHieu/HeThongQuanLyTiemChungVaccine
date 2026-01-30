package com.josephhieu.vaccinebackend.modules.vaccination.entity;

import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "LICHTIEMCHUNG")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichTiemChung {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaLichTiem", length = 36, nullable = false)
    private UUID maLichTiem;

    @Column(name = "DoiTuong", length = 255)
    private String doiTuong;

    @Column(name = "ThoiGianChung", length = 255)
    private String thoiGianChung;

    @Column(name = "GhiChu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "SoLuongNguoiTiem")
    private Integer soLuongNguoiTiem;

    @Column(name = "NgayTiem")
    private LocalDate ngayTiem;

    @Column(name = "DiaDiem", length = 255)
    private String diaDiem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLo")
    private LoVacXin loVacXin;
}
