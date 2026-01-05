package com.josephhieu.vaccinebackend.entity;

import com.josephhieu.vaccinebackend.entity.id.ChiTietPhanQuyenId;
import jakarta.persistence.*;
import lombok.*;

/**
 * Thực thể trung gian ánh xạ đến bảng CHITIETPHANQUYEN.
 * Kết nối quan hệ Nhiều-Nhiều giữa TaiKhoan và PhanQuyen.
 * * @author Joseph Hieu
 */
@Entity
@Table(name = "CHITIETPHANQUYEN")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhanQuyen {

    /**
     * Khóa chính phức hợp bao gồm MaQuyen và MaTaiKhoan.
     */
    @EmbeddedId
    private ChiTietPhanQuyenId id;

    /**
     * Quan hệ Many-to-One trỏ về thực thể PhanQuyen.
     */
    @ManyToOne
    @MapsId("maQuyen")
    @JoinColumn(name = "MaQuyen")
    private PhanQuyen phanQuyen;

    /**
     * Quan hệ Many-to-One trỏ về thực thể TaiKhoan.
     */
    @ManyToOne
    @MapsId("maTaiKhoan")
    @JoinColumn(name = "MaTaiKhoan")
    private TaiKhoan taiKhoan;
}
