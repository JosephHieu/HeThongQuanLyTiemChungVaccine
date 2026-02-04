package com.josephhieu.vaccinebackend.modules.vaccination.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationHistoryResponse {

    private UUID maDangKy;
    private String tenVacXin;
    private String soLo;
    private String ngayTiem;
    private String thoiGian;
    private String diaDiem;
    private String trangThai;
}
