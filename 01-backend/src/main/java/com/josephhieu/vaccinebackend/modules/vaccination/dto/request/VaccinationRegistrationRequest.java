package com.josephhieu.vaccinebackend.modules.vaccination.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationRegistrationRequest {

    @NotNull(message = "Vui lòng chọn vắc-xin cần tiêm")
    private UUID maVacXin;

    @NotNull(message = "Vui lòng chọn lịch tiêm chủng")
    private UUID maLichTiemChung;

    private String ghiChu;
}
