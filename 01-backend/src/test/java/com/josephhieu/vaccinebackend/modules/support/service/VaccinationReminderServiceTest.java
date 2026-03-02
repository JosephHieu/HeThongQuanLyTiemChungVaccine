package com.josephhieu.vaccinebackend.modules.support.service;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
import com.josephhieu.vaccinebackend.modules.support.dto.request.VaccinationReminderRequest;
import com.josephhieu.vaccinebackend.modules.support.dto.response.VaccinationReminderResponse;
import com.josephhieu.vaccinebackend.modules.support.service.impl.VaccinationReminderServiceImpl;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietDangKyTiemRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VaccinationReminderServiceTest {

    @Mock private BenhNhanRepository benhNhanRepository;
    @Mock private HoSoBenhAnRepository hoSoBenhAnRepository;
    @Mock private ChiTietDangKyTiemRepository chiTietDangKyTiemRepository;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private VaccinationReminderServiceImpl reminderService;

    private BenhNhan mockBenhNhan;
    private String testEmail = "joseph.hieu@example.com";

    @BeforeEach
    void setUp() {
        TaiKhoan tk = TaiKhoan.builder().email(testEmail).build();
        mockBenhNhan = BenhNhan.builder()
                .maBenhNhan(UUID.randomUUID())
                .tenBenhNhan("Joseph Hieu")
                .taiKhoan(tk)
                .build();
    }

    @Test
    @DisplayName("Get Data: Thất bại khi không tìm thấy bệnh nhân theo email")
    void getPatientDataByEmail_UserNotFound_ThrowException() {
        when(benhNhanRepository.findByTaiKhoan_Email(anyString())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () ->
                reminderService.getPatientDataByEmail(testEmail)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }

    @Test
    @DisplayName("Get Data: Thành công và trả về đúng thông tin cá nhân")
    void getPatientDataByEmail_Success() {
        // GIVEN
        when(benhNhanRepository.findByTaiKhoan_Email(testEmail)).thenReturn(Optional.of(mockBenhNhan));
        when(hoSoBenhAnRepository.findHistoryByPatient(any())).thenReturn(new ArrayList<>());
        when(chiTietDangKyTiemRepository.findPendingAppointments(any())).thenReturn(new ArrayList<>());

        // WHEN
        VaccinationReminderResponse response = reminderService.getPatientDataByEmail(testEmail);

        // THEN
        assertThat(response.getHoTen()).isEqualTo("Joseph Hieu");
        assertThat(response.getEmail()).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("Send Email: Ném lỗi khi tiến trình gửi mail gặp sự cố hệ thống")
    void sendReminderEmail_MailSystemError_ThrowException() {
        // GIVEN
        VaccinationReminderRequest request = VaccinationReminderRequest.builder()
                .email(testEmail)
                .build();

        when(benhNhanRepository.findByTaiKhoan_Email(testEmail)).thenReturn(Optional.of(mockBenhNhan));
        // Mock getPatientDataByEmail bằng cách chuẩn bị các repo liên quan
        when(hoSoBenhAnRepository.findHistoryByPatient(any())).thenReturn(new ArrayList<>());
        when(chiTietDangKyTiemRepository.findPendingAppointments(any())).thenReturn(new ArrayList<>());

        // Giả lập lỗi khi tạo MimeMessage
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP Server Down"));

        // WHEN & THEN
        AppException ex = assertThrows(AppException.class, () ->
                reminderService.sendReminderEmail(request)
        );
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_SEND_FAILED);
    }

    @Test
    @DisplayName("Send Email: Thành công gọi hàm gửi mail")
    void sendReminderEmail_Success() {
        // GIVEN
        VaccinationReminderRequest request = VaccinationReminderRequest.builder()
                .email(testEmail)
                .loiNhan("Đã đến ngày tiêm nhắc lại mũi 2")
                .build();

        MimeMessage mockMimeMessage = mock(MimeMessage.class);

        when(benhNhanRepository.findByTaiKhoan_Email(testEmail)).thenReturn(Optional.of(mockBenhNhan));
        when(hoSoBenhAnRepository.findHistoryByPatient(any())).thenReturn(new ArrayList<>());
        when(chiTietDangKyTiemRepository.findPendingAppointments(any())).thenReturn(new ArrayList<>());
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        // WHEN
        reminderService.sendReminderEmail(request);

        // THEN
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}