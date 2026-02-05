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

    // 1. Dùng maLoVacXin để khớp với Entity LoVacXin
    @NotNull(message = "MISSING_INFO")
    private UUID maLoVacXin;

    // 2. Cho phép null vì Tra cứu vắc-xin sẽ không có lịch tiêm
    private UUID maLichTiemChung;

    // 3. Bắt buộc phải có ngày để hệ thống biết khi nào bệnh nhân đến
    @NotNull(message = "MISSING_INFO")
    private java.time.LocalDate thoiGianCanTiem;

    private String ghiChu;
}
