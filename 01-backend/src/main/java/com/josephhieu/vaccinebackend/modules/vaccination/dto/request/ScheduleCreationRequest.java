package com.josephhieu.vaccinebackend.modules.vaccination.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO để tạo mới một lịch tiêm chủng.
 * Chứa thông tin cơ bản và danh sách bác sĩ được phân công.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreationRequest {

    private LocalDate ngayTiem; // Ngày tổ chức tiêm
    private String thoiGian;    // Thời gian cụ thể (Sáng/Chiều hoặc khung giờ)
    private Integer soLuong;    // Số lượng vắc xin dự kiến cho đợt này
    private String doTuoi;      // Độ tuổi khuyên dùng (VD: Trẻ em < 5 tuổi)
    private String diaDiem;     // Địa điểm tổ chức đợt tiêm
    private String ghiChu;      // Các lưu ý đặc biệt cho đợt tiêm này

    /**
     * Danh sách ID của các nhân viên/bác sĩ tham gia trực đợt tiêm.
     * Ánh xạ tới phần "Bác sĩ" trên giao diện.
     */
    private List<UUID> danhSachBacSiIds;
}
