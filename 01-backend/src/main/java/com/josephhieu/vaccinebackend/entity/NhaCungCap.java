package com.josephhieu.vaccinebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "NHACUNGCAP")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaNhaCungCap", length = 36, nullable = false)
    private UUID maNhaCungCap;

    @Column(name = "TenNhaCungCap", length = 255)
    private String tenNhaCungCap;
}
