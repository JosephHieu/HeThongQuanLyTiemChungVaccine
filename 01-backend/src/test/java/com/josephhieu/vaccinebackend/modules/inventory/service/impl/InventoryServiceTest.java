package com.josephhieu.vaccinebackend.modules.inventory.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineExportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineImportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.entity.*;
import com.josephhieu.vaccinebackend.modules.inventory.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock private LoVacXinRepository loVacXinRepository;
    @Mock private VacXinRepository vacXinRepository;
    @Mock private LoaiVacXinRepository loaiVacXinRepository;
    @Mock private NhaCungCapRepository nhaCungCapRepository;
    @Mock private PhieuXuatRepository phieuXuatRepository;
    @Mock private HoaDonRepository hoaDonRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private VaccineImportRequest importRequest;
    private LoVacXin mockLo;
    private VacXin mockVacXin;

    @BeforeEach
    void setUp() {
        importRequest = VaccineImportRequest.builder()
                .tenVacXin("AstraZeneca")
                .soLo("BATCH123")
                .soLuong(100)
                .giaNhap(new BigDecimal("100000"))
                .donGia(new BigDecimal("150000"))
                .maNhaCungCap(UUID.randomUUID())
                .maLoaiVacXin(UUID.randomUUID())
                .hanSuDung(LocalDate.now().plusYears(1))
                .build();

        mockVacXin = VacXin.builder()
                .tenVacXin("AstraZeneca")
                .loaiVacXin(new LoaiVacXin())
                .build();

        mockLo = LoVacXin.builder()
                .maLo(UUID.randomUUID())
                .soLo("BATCH123")
                .vacXin(mockVacXin)
                .soLuong(100)
                .build();
    }

    // --- TEST NHẬP KHO (IMPORT) ---

    @Test
    @DisplayName("Import: Lỗi khi số lô đã tồn tại")
    void importVaccine_Fail_DuplicateBatch() {
        when(loVacXinRepository.existsBySoLo(anyString())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            inventoryService.importVaccine(importRequest);
        });

        assertEquals(ErrorCode.BATCH_ALREADY_EXISTS, exception.getErrorCode());
    }

    // --- TEST XUẤT KHO (EXPORT) ---

    @Test
    @DisplayName("Export: Lỗi khi số lượng xuất lớn hơn tồn kho")
    void exportVaccine_Fail_InsufficientStock() {
        VaccineExportRequest exportRequest = VaccineExportRequest.builder()
                .maLo(mockLo.getMaLo())
                .soLuongXuat(150) // Xuất 150 trong khi chỉ có 100
                .build();

        when(loVacXinRepository.findById(any())).thenReturn(Optional.of(mockLo));

        AppException exception = assertThrows(AppException.class, () -> {
            inventoryService.exportVaccine(exportRequest);
        });

        assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }

    @Test
    @DisplayName("Export: Thành công - Trừ kho và tạo phiếu xuất")
    void exportVaccine_Success() {
        VaccineExportRequest exportRequest = VaccineExportRequest.builder()
                .maLo(mockLo.getMaLo())
                .soLuongXuat(30)
                .noiNhan("Phong 01")
                .build();

        when(loVacXinRepository.findById(any())).thenReturn(Optional.of(mockLo));
        when(phieuXuatRepository.save(any(PhieuXuat.class))).thenAnswer(i -> {
            PhieuXuat p = (PhieuXuat) i.getArguments()[0];
            p.setMaPhieuXuat(UUID.randomUUID());
            return p;
        });

        var response = inventoryService.exportVaccine(exportRequest);

        // Kiểm tra số lượng còn lại: 100 - 30 = 70
        assertEquals(70, response.getSoLuongConLaiTrongKho());
        verify(loVacXinRepository).save(argThat(lo -> lo.getSoLuong() == 70));
        verify(phieuXuatRepository).save(any(PhieuXuat.class));
    }
}