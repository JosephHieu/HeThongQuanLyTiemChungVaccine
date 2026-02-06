package com.josephhieu.vaccinebackend.modules.medical.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotBlank(message = "Tên vắc-xin không được để trống")
    private String tenVacXin;

    private String thoiGianTiem;

    private String diaDiemTiem;

    private String nhanVienPhuTrach;

    @NotBlank(message = "Nội dung phản hồi không được để trống")
    private String noiDung;
}
