package com.josephhieu.vaccinebackend.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "LOAIVACXIN")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LoaiVacXin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MaLoaiVacXin", length = 36, nullable = false)
    private UUID MaLoaiVacXin;

    @Column(name = "TenLoaiVacXin", length = 255)
    private String tenLoaiVacXin;
}
