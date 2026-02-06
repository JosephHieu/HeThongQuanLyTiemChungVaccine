package com.josephhieu.vaccinebackend.modules.medical.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "LOAIPHANHOI")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoaiPhanHoi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaLoaiPhanHoi", length = 36,  nullable = false)
    private UUID maLoaiPhanHoi;

    @Column(name = "TenLoaiPhanHoi", length = 255)
    private String tenLoaiPhanHoi;
}
