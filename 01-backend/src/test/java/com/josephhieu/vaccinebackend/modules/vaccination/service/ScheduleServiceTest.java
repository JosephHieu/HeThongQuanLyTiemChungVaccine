package com.josephhieu.vaccinebackend.modules.vaccination.service;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.dto.request.ScheduleCreationRequest;
import com.josephhieu.vaccinebackend.modules.vaccination.entity.LichTiemChung;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.ChiTietNhanVienThamGiaRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.repository.LichTiemChungRepository;
import com.josephhieu.vaccinebackend.modules.vaccination.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock private LichTiemChungRepository lichTiemChungRepository;
    @Mock private LoVacXinRepository loVacXinRepository;
    @Mock private NhanVienRepository nhanVienRepository;
    @Mock private ChiTietNhanVienThamGiaRepository chiTietNhanVienThamGiaRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private ScheduleCreationRequest request;
    private LoVacXin mockLo;
    private UUID mockBatchId;

    @BeforeEach
    void setUp() {
        mockBatchId = UUID.randomUUID();
        mockLo = LoVacXin.builder()
                .maLo(mockBatchId)
                .soLuong(100) // Tồn kho 100 liều
                .build();

        request = ScheduleCreationRequest.builder()
                .ngayTiem(LocalDate.now().plusDays(7))
                .thoiGian("Sáng")
                .soLuong(50)
                .maLo(mockBatchId)
                .danhSachBacSiIds(List.of(UUID.randomUUID()))
                .build();
    }

    @Test
    @DisplayName("Create: Lỗi khi trùng ngày và ca tiêm")
    void createSchedule_Fail_Duplicate() {
        when(lichTiemChungRepository.existsByNgayTiemAndThoiGianChung(any(), any())).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> scheduleService.createScheduleService(request));
        assertEquals(ErrorCode.SCHEDULE_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    @DisplayName("Create: Lỗi khi số lượng đăng ký vượt quá tồn kho")
    void createSchedule_Fail_InsufficientStock() {
        request.setSoLuong(150); // Yêu cầu 150 liều trong khi kho có 100
        when(loVacXinRepository.findById(mockBatchId)).thenReturn(Optional.of(mockLo));

        AppException ex = assertThrows(AppException.class, () -> scheduleService.createScheduleService(request));
        assertEquals(ErrorCode.INSUFFICIENT_STOCK, ex.getErrorCode());
    }

    @Test
    @DisplayName("Create: Thành công và lưu thông tin bác sĩ trực")
    void createSchedule_Success() {
        // GIVEN
        LichTiemChung savedLich = LichTiemChung.builder().maLichTiem(UUID.randomUUID()).build();
        when(lichTiemChungRepository.existsByNgayTiemAndThoiGianChung(any(), any())).thenReturn(false);
        when(loVacXinRepository.findById(mockBatchId)).thenReturn(Optional.of(mockLo));
        when(lichTiemChungRepository.save(any())).thenReturn(savedLich);
        when(nhanVienRepository.findById(any())).thenReturn(Optional.of(new NhanVien()));

        // WHEN
        var response = scheduleService.createScheduleService(request);

        // THEN
        assertNotNull(response);
        verify(lichTiemChungRepository).save(any(LichTiemChung.class));
        verify(chiTietNhanVienThamGiaRepository, times(1)).save(any());
    }
}