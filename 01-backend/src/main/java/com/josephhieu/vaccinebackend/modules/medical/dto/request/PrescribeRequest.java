package com.josephhieu.vaccinebackend.modules.medical.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescribeRequest {

    @NotNull(message = "Vui lòng chọn lô vắc-xin")
    private UUID maLoVacXin;

    @NotNull(message = "Vui lòng chọn ngày hẹn tiêm")
    private LocalDate thoiGianCanTiem;

    private String ghiChu;
}
