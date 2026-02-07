package com.josephhieu.vaccinebackend.modules.medical.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class HighLevelFeedbackRequest {

    @NotNull(message = "Vui lòng chọn loại phản hồi")
    private UUID maLoaiPhanHoi;

    @NotBlank(message = "Nội dung phản hồi không được để trống")
    private String noiDung;
}
