package com.josephhieu.vaccinebackend.modules.support.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.support.dto.request.VaccinationReminderRequest;
import com.josephhieu.vaccinebackend.modules.support.dto.response.VaccinationReminderResponse;
import com.josephhieu.vaccinebackend.modules.support.service.VaccinationReminderService;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccinationReminderServiceImpl implements VaccinationReminderService {

    private final BenhNhanRepository benhNhanRepository;
    private final HoSoBenhAnRepository hoSoBenhAnRepository;
    private final ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    private final JavaMailSender mailSender;


    @Override
    public VaccinationReminderResponse getPatientDataByEmail(String email) {

        // 1. Tìm bệnh nhân
        BenhNhan bn = benhNhanRepository.findByTaiKhoan_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Lấy lịch sử từ HoSoBenhAn (Mũi đã tiêm)
        var history = hoSoBenhAnRepository.findHistoryByPatient(bn.getMaBenhNhan()).stream()
                .map(hs -> VaccinationReminderResponse.InjectedHistory.builder()
                        .ngayTiem(hs.getThoiGianTiem().toLocalDate())
                        .tenVacXin(hs.getChiTietDangKyTiem().getLoVacXin().getVacXin().getTenVacXin())
                        .trangThai("Đã hoàn thành")
                        .build())
                .collect(Collectors.toList());

        // 3. Lấy lịch dự kiến từ ChiTietDangKyTiem (Mũi REGISTERED & chưa có hồ sơ)
        var upcoming = chiTietDangKyTiemRepository.findPendingAppointments(bn.getMaBenhNhan()).stream()
                .map(ct -> VaccinationReminderResponse.UpcomingSchedule.builder()
                        .ngayDuKien(ct.getThoiGianCanTiem())
                        .tenVacXin(ct.getLoVacXin().getVacXin().getTenVacXin())
                        .giaTienDuKien(ct.getLoVacXin().getVacXin().getDonGia())
                        .build())

                .collect(Collectors.toList());

        return VaccinationReminderResponse.builder()
                .hoTen(bn.getTenBenhNhan())
                .email(bn.getTaiKhoan().getEmail())
                .soDienThoai(bn.getSdt())
                .lichSuTiem(history)
                .lichDuKien(upcoming)
                .build();
    }

    @Override
    public void sendReminderEmail(VaccinationReminderRequest request) {
        // 1. Kiểm tra dữ liệu trước khi gửi
        BenhNhan bn = benhNhanRepository.findByTaiKhoan_Email(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var patientData = this.getPatientDataByEmail(request.getEmail());

        // Nếu không có lịch dự kiến, có thể chặn không cho gửi để tránh spam email trống
        if (patientData.getLichDuKien().isEmpty()) {
            throw new AppException(ErrorCode.REMINDER_DATA_EMPTY);
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getEmail());
            helper.setSubject(request.getTieuDe() != null ? request.getTieuDe() : "Nhắc lịch tiêm chủng - Trung tâm JosephHieu");

            String htmlContent = buildHtmlContent(bn, patientData.getLichDuKien(), request.getLoiNhan());
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Lỗi hệ thống gửi mail: {}", e.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String buildHtmlContent(BenhNhan bn, List<VaccinationReminderResponse.UpcomingSchedule> upcomingList, String loiNhan) {
        StringBuilder tableRows = new StringBuilder();
        for (int i = 0; i < upcomingList.size(); i++) {
            var item = upcomingList.get(i);
            tableRows.append("<tr>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee; text-align: center;'>").append(i + 1).append("</td>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee; font-weight: bold; color: #e11d48;'>").append(item.getNgayDuKien()).append("</td>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee;'>").append(item.getTenVacXin()).append("</td>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee; color: #059669; font-weight: bold;'>")
                    .append(String.format("%,.0f VNĐ", item.getGiaTienDuKien())).append("</td>")
                    .append("</tr>");
        }

        return "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #334155; max-width: 600px; margin: auto; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden;'>"
                + "  <div style='background-color: #059669; color: white; padding: 20px; text-align: center;'>"
                + "    <h2 style='margin: 0; text-transform: uppercase;'>Nhắc lịch tiêm chủng</h2>"
                + "  </div>"
                + "  <div style='padding: 20px;'>"
                + "    <p>Chào <strong>" + bn.getTenBenhNhan() + "</strong>,</p>"
                + "    <p>" + (loiNhan != null ? loiNhan : "Trung tâm tiêm chủng gửi bạn thông tin các mũi tiêm dự kiến sắp tới. Vui lòng sắp xếp thời gian để đảm bảo hiệu quả phòng bệnh tốt nhất.") + "</p>"
                + "    <h3 style='color: #1e293b; border-left: 4px solid #059669; padding-left: 10px;'>Lịch tiêm dự kiến:</h3>"
                + "    <table style='width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 14px;'>"
                + "      <thead>"
                + "        <tr style='background-color: #f8fafc;'>"
                + "          <th style='padding: 10px; border-bottom: 2px solid #cbd5e1;'>STT</th>"
                + "          <th style='padding: 10px; border-bottom: 2px solid #cbd5e1;'>Ngày tiêm</th>"
                + "          <th style='padding: 10px; border-bottom: 2px solid #cbd5e1;'>Vắc xin</th>"
                + "          <th style='padding: 10px; border-bottom: 2px solid #cbd5e1;'>Giá dự kiến</th>"
                + "        </tr>"
                + "      </thead>"
                + "      <tbody>" + tableRows.toString() + "</tbody>"
                + "    </table>"
                + "    <div style='margin-top: 30px; text-align: center;'>"
                + "      <a href='http://localhost:3000/login' style='background-color: #1e4e8c; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>ĐĂNG NHẬP PORTAL</a>"
                + "    </div>"
                + "  </div>"
                + "  <div style='background-color: #f1f5f9; padding: 15px; text-align: center; font-size: 12px; color: #64748b;'>"
                + "    <p style='margin: 0;'>Đây là email tự động từ hệ thống quản lý tiêm chủng.</p>"
                + "    <p style='margin: 5px 0;'>Địa chỉ: 123 Đường ABC, Quận X, Thành phố Y</p>"
                + "  </div>"
                + "</div>";
    }
}
