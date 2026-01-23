package com.josephhieu.vaccinebackend.modules.vaccination.entity;

import com.josephhieu.vaccinebackend.modules.vaccination.entity.id.NhanVienThamGiaId;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHITIET_NV_THAMGIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
