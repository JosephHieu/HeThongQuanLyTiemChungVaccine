package com.josephhieu.vaccinebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "LOAIVACXIN")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoaiVacXin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaLoaiVacXin", length = 36, nullable = false)
    private UUID MaLoaiVacXin;

    @Column(name = "TenLoaiVacXin", length = 255)
    private String tenLoaiVacXin;
}
