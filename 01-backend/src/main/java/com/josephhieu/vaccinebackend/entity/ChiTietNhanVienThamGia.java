package com.josephhieu.vaccinebackend.entity;

import com.josephhieu.vaccinebackend.entity.id.NhanVienThamGiaId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CHITIET_NV_THAMGIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietNhanVienThamGia {

    @EmbeddedId
    private NhanVienThamGiaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maNhanVien")
    @JoinColumn(name = "MaNhanVien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maLichTiem")
    @JoinColumn(name = "MaLichTiem")
    private LichTiemChung lichTiemChung;
}
