package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.FeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.entity.PhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.repository.LoaiPhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.PhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.impl.FeedbackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock private PhanHoiRepository phanHoiRepository;
    @Mock private LoaiPhanHoiRepository loaiPhanHoiRepository;
    @Mock private BenhNhanRepository benhNhanRepository;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        // Giả lập SecurityContext để lấy username
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("patient_test");
    }

    @Test
    @DisplayName("Send Feedback: Thành công khi gửi phản hồi đúng định dạng")
    void sendFeedback_Success() {
        // GIVEN
        FeedbackRequest request = FeedbackRequest.builder()
                .tenVacXin("VNVC Pfizer")
                .thoiGianTiem("15/05/2026 09:30")
                .diaDiemTiem("Cơ sở Quận 1")
                .noiDung("Sốt nhẹ sau tiêm")
                .build();

        BenhNhan mockBenhNhan = BenhNhan.builder()
                .maBenhNhan(UUID.randomUUID())
                .tenBenhNhan("Nguyễn Văn A")
                .build();

        when(benhNhanRepository.findByTaiKhoan_TenDangNhap("patient_test"))
                .thenReturn(Optional.of(mockBenhNhan));

        when(loaiPhanHoiRepository.findByTenLoaiPhanHoi(anyString()))
                .thenReturn(Optional.of(new LoaiPhanHoi(UUID.randomUUID(), "Phản hồi sau tiêm")));

        // WHEN
        feedbackService.sendFeedback(request);

        // THEN
        // Kiểm tra xem phanHoiRepository.save() có được gọi với đúng ngày đã parse không
        verify(phanHoiRepository).save(argThat(ph ->
                ph.getTenVacXin().equals("VNVC Pfizer") &&
                        ph.getThoiGianTiem().equals(LocalDate.of(2026, 5, 15)) && // Quan trọng: Check parseDate
                        ph.getTrangThai() == 0 &&
                        ph.getBenhNhan().getTenBenhNhan().equals("Nguyễn Văn A")
        ));
    }

    @Test
    @DisplayName("Send Feedback: Tự động dùng ngày hiện tại nếu thoiGianTiem bị lỗi/trống")
    void sendFeedback_FallbackDate_Success() {
        FeedbackRequest request = FeedbackRequest.builder()
                .tenVacXin("Astra")
                .thoiGianTiem("") // Chuỗi rỗng
                .noiDung("Bình thường")
                .build();

        when(benhNhanRepository.findByTaiKhoan_TenDangNhap(anyString()))
                .thenReturn(Optional.of(new BenhNhan()));
        when(loaiPhanHoiRepository.findByTenLoaiPhanHoi(anyString()))
                .thenReturn(Optional.of(new LoaiPhanHoi()));

        feedbackService.sendFeedback(request);

        // Assert: thoiGianTiem phải là LocalDate.now() do logic try-catch của bạn
        verify(phanHoiRepository).save(argThat(ph ->
                ph.getThoiGianTiem().equals(LocalDate.now())
        ));
    }
}