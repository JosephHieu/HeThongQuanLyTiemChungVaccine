package com.josephhieu.vaccinebackend.modules.vaccination.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {
    private UUID maDangKy;
    private String tenBenhNhan;
    private String soDienThoai;
    private String tenVacXin;   // Lấy từ LoVacXin -> VacXin
    private LocalDateTime ngayDangKy;
}