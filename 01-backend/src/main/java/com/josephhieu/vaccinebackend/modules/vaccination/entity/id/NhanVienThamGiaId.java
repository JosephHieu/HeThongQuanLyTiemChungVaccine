package com.josephhieu.vaccinebackend.modules.vaccination.entity.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NhanVienThamGiaId implements Serializable {

    private UUID maNhanVien;
    private UUID maLichTiem;
}
