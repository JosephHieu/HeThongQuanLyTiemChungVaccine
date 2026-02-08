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
public class HighLevelFeedbackResponse {

    private UUID maPhanHoi;

    // Tên loại phản hồi (Khen ngợi, Phàn nàn...) để hiển thị UI
    private String tenLoaiPhanHoi;

    private String noiDung;

    // Trạng thái theo quy ước (0: Mới, 1: Đang xử lý, 2: Đã giải quyết)
    private Integer trangThai;

    // Thông tin người gửi
    private String tenBenhNhan;
    private String sdtBenhNhan;
    private String emailBenhNhan;
    // Thời gian phản hồi (Format: dd/MM/yyyy HH:mm)
    private String thoiGianGui;
}
