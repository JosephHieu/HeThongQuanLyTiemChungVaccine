package com.josephhieu.vaccinebackend.modules.support.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationReminderResponse {

    private String hoTen;
    private String email;
    private String soDienThoai;

    // Danh sách các mũi tiêm đã tiêm
    private List<InjectedHistory> lichSuTiem;

    // Danh sách các mũi dự kiến sắp tới
    private List<UpcomingSchedule> lichDuKien;

    @Data
    @Builder
    public static class InjectedHistory {
        private LocalDate ngayTiem;
        private String tenVacXin;
        private String trangThai;
    }

    @Data
    @Builder
    public static class UpcomingSchedule {
        private LocalDate ngayDuKien;
        private String tenVacXin;
        private BigDecimal giaTienDuKien;
    }
}
