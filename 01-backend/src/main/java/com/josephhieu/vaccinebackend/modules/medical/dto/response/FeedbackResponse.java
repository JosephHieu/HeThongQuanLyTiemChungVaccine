package com.josephhieu.vaccinebackend.modules.medical.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private UUID maPhanHoi;
    private String tenVacXin;
    private String thoiGianTiem;
    private String noiDung;
    private String loaiPhanHoi;
    private String trangThai;
}
