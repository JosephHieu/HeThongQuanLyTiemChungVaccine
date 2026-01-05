package com.josephhieu.vaccinebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

/**
 * Thực thể ánh xạ đến bảng PHANQUYEN trong cơ sở dữ liệu.
 * Lưu trữ các loại quyền như Administrator, Moderator, và Normal User Account. [cite: 60]
 * * @author Joseph Hieu
 */

@Entity
@Table(name = "PHANQUYEN")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhanQuyen {

    /**
     * Mã định danh duy nhất cho quyền hạn.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaQuyen", length = 36, nullable = false, updatable = false)
    private UUID maQuyen;

    /**
     * Tên gọi của quyền hạn (ví dụ: Administrator).
     */
    @Column(name = "TenQuyen", length = 255)
    private String tenQuyen;

    /**
     * Danh sách các chi tiết phân quyền liên quan.
     */
    @OneToMany(mappedBy = "phanQuyen")
    private Set<ChiTietPhanQuyen> chiTietPhanQuyens;
}
