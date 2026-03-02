package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.entity.TaiKhoan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.HighLevelFeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.entity.PhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.repository.LoaiPhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.PhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.impl.HighLevelFeedbackServiceImpl;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HighLevelFeedbackServiceTest {

    @Mock private PhanHoiRepository phanHoiRepository;
    @Mock private BenhNhanRepository benhNhanRepository;
    @Mock private LoaiPhanHoiRepository loaiPhanHoiRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private HighLevelFeedbackServiceImpl highLevelFeedbackService;

    private String currentUser = "hieu_patient";
    private PhanHoi mockPhanHoi;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(currentUser);

        TaiKhoan tk = TaiKhoan.builder().tenDangNhap(currentUser).build();
        BenhNhan bn = BenhNhan.builder().taiKhoan(tk).tenBenhNhan("Joseph Hieu").build();

        mockPhanHoi = PhanHoi.builder()
                .maPhanHoi(UUID.randomUUID())
                .benhNhan(bn)
                .trangThai(0)
                .build();
    }

    @Test
    @DisplayName("Update: Lỗi khi bệnh nhân sửa phản hồi của người khác")
    void updateFeedback_UnAuthorized_ThrowException() {
        // GIVEN: Phản hồi này thuộc về người dùng "another_user"
        mockPhanHoi.getBenhNhan().getTaiKhoan().setTenDangNhap("another_user");
        when(phanHoiRepository.findById(any())).thenReturn(Optional.of(mockPhanHoi));

        HighLevelFeedbackRequest request = new HighLevelFeedbackRequest();

        // WHEN & THEN
        AppException exception = assertThrows(AppException.class, () ->
                highLevelFeedbackService.updateFeedback(mockPhanHoi.getMaPhanHoi(), request)
        );
        assert(exception.getErrorCode() == ErrorCode.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Update: Lỗi khi sửa phản hồi đã được Admin xử lý (status != 0)")
    void updateFeedback_InvalidStatus_ThrowException() {
        // GIVEN: Phản hồi đã chuyển sang trạng thái "Đang xử lý" (1)
        mockPhanHoi.setTrangThai(1);
        when(phanHoiRepository.findById(any())).thenReturn(Optional.of(mockPhanHoi));

        HighLevelFeedbackRequest request = new HighLevelFeedbackRequest();

        // WHEN & THEN
        AppException exception = assertThrows(AppException.class, () ->
                highLevelFeedbackService.updateFeedback(mockPhanHoi.getMaPhanHoi(), request)
        );
        assert(exception.getErrorCode() == ErrorCode.INVALID_REGISTRATION_STATUS);
    }

    @Test
    @DisplayName("Delete: Thành công khi bệnh nhân xóa phản hồi chính chủ và chưa xử lý")
    void deleteMyFeedback_Success() {
        // GIVEN
        when(phanHoiRepository.findById(any())).thenReturn(Optional.of(mockPhanHoi));

        // WHEN
        highLevelFeedbackService.deleteMyFeedback(mockPhanHoi.getMaPhanHoi());

        // THEN
        verify(phanHoiRepository).delete(mockPhanHoi);
    }

    @Test
    @DisplayName("Admin: Cập nhật trạng thái thành công")
    void updateStatus_ByAdmin_Success() {
        // GIVEN
        when(phanHoiRepository.findById(any())).thenReturn(Optional.of(mockPhanHoi));

        // WHEN
        highLevelFeedbackService.updateStatus(mockPhanHoi.getMaPhanHoi(), 2);

        // THEN
        assert(mockPhanHoi.getTrangThai() == 2);
        verify(phanHoiRepository).save(mockPhanHoi);
    }
}