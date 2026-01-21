package com.josephhieu.vaccinebackend.modules.identity.entity.id;


import com.josephhieu.vaccinebackend.modules.identity.entity.ChiTietPhanQuyen;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Lớp ID Phức hợp (Composite Primary Key) cho Entity {@link ChiTietPhanQuyen}.
 * Ánh xạ khóa chính hỗn hợp gồm MaQuyen và MaTaiKhoan từ bảng CHITIETPHANQUYEN.
 * * @author Joseph Hieu
 * @version 1.0
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietPhanQuyenId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Mã quyền hạn (Khóa ngoại trỏ đến bảng PHANQUYEN).
     */
    @Column(name = "MaQuyen", length = 36)
    private UUID maQuyen;

    /**
     * Mã tài khoản người dùng (Khóa ngoại trỏ đến bảng TAIKHOAN).
     */
    @Column(name = "MaTaiKhoan", length = 36)
    private UUID maTaiKhoan;
}
