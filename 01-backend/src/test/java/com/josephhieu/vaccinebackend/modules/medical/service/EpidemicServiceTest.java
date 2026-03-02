package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.NhanVien;
import com.josephhieu.vaccinebackend.modules.identity.repository.NhanVienRepository;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.EpidemicRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.EpidemicResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.DichBenh;
import com.josephhieu.vaccinebackend.modules.medical.repository.DichBenhRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.impl.EpidemicServiceImpl;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EpidemicServiceTest {

    @Mock private DichBenhRepository dichBenhRepository;
    @Mock private VacXinRepository vacXinRepository;
    @Mock private NhanVienRepository nhanVienRepository;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private EpidemicServiceImpl epidemicService;

    private DichBenh mockDichBenh;
    private NhanVien mockNhanVien;
    private EpidemicRequest request;

    @BeforeEach
    void setUp() {
        mockNhanVien = NhanVien.builder()
                .maNhanVien(UUID.randomUUID())
                .tenNhanVien("Bác sĩ Hiếu")
                .build();

        mockDichBenh = DichBenh.builder()
                .maDichBenh(UUID.randomUUID())
                .tenDichBenh("Cúm A")
                .diaChi("Hà Nội")
                .thoiDiemKhaoSat(LocalDate.of(2026, 3, 1))
                .nhanVien(mockNhanVien)
                .build();

        request = EpidemicRequest.builder()
                .tenDichBenh("Cúm A")
                .diaChi("Hà Nội")
                .thoiDiemKhaoSat(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Create: Lưu dịch bệnh thành công và gán đúng nhân viên đang đăng nhập")
    void createEpidemic_Success() {
        // 1. Giả lập Security Context lấy tên người dùng "hieu_staff"
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("hieu_staff");

        // 2. Giả lập tìm thấy nhân viên trong DB
        when(nhanVienRepository.findByTaiKhoan_TenDangNhap("hieu_staff"))
                .thenReturn(Optional.of(mockNhanVien));

        // WHEN
        epidemicService.createEpidemic(request);

        // THEN
        verify(dichBenhRepository).save(argThat(db ->
                db.getTenDichBenh().equals("Cúm A") &&
                        db.getNhanVien().getTenNhanVien().equals("Bác sĩ Hiếu")
        ));
    }

    @Test
    @DisplayName("Logic: Tự động gợi ý vắc-xin dựa trên tên dịch bệnh")
    void getEpidemicById_WithVaccineSuggestions() {
        // 1. Giả lập tìm thấy dịch bệnh
        when(dichBenhRepository.findById(any())).thenReturn(Optional.of(mockDichBenh));

        // 2. Giả lập tìm thấy 2 loại vắc-xin phòng bệnh Cúm A
        VacXin v1 = VacXin.builder().tenVacXin("Vắc-xin Cúm Tam Giá").build();
        VacXin v2 = VacXin.builder().tenVacXin("Vắc-xin Cúm Tứ Giá").build();
        when(vacXinRepository.findByPhongNguaBenhContainingIgnoreCase("Cúm A"))
                .thenReturn(List.of(v1, v2));

        // WHEN
        EpidemicResponse response = epidemicService.getEpidemicById(UUID.randomUUID());

        // THEN
        assertThat(response.getVacXinGoiY()).hasSize(2);
        assertThat(response.getVacXinGoiY()).contains("Vắc-xin Cúm Tam Giá", "Vắc-xin Cúm Tứ Giá");
        assertThat(response.getTenNhanVienKhaoSat()).isEqualTo("Bác sĩ Hiếu");
    }

    @Test
    @DisplayName("Error: Ném lỗi khi không tìm thấy dịch bệnh để xóa")
    void deleteEpidemic_NotFound_ThrowException() {
        when(dichBenhRepository.existsById(any())).thenReturn(false);

        assertThrows(AppException.class, () -> epidemicService.deleteEpidemic(UUID.randomUUID()));
    }
}