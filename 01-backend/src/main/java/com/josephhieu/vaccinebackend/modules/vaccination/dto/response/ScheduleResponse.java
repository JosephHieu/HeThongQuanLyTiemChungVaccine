package com.josephhieu.vaccinebackend.modules.vaccination.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO trả về thông tin chi tiết của một lịch tiêm chủng.
 * Tránh trả về Entity trực tiếp để bảo mật và tối ưu hiệu năng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private UUID maLichTiemChung;
    private LocalDate ngayTiem;
    private String thoiGian;
    private String tenVacXin;     // Trả về tên thay vì chỉ trả về ID
    private Integer soLuong;
    private Integer daDangKy;     // Tổng số bệnh nhân đã đăng ký thực tế
    private String doTuoi;
    private String diaDiem;
    private String ghiChu;

    /**
     * Thông tin tóm tắt của các bác sĩ tham gia.
     */
    private List<StaffSummaryResponse> danhSachBacSi;
}
