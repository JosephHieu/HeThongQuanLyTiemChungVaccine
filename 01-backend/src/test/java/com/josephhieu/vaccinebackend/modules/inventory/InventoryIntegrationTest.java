package com.josephhieu.vaccinebackend.modules.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineExportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineImportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.NhaCungCap;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Đảm bảo dữ liệu rollback sau mỗi bài test
public class InventoryIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private LoaiVacXinRepository loaiVacXinRepository;
    @Autowired private NhaCungCapRepository nhaCungCapRepository;
    @Autowired private LoVacXinRepository loVacXinRepository;
    @Autowired private HoaDonRepository hoaDonRepository;
    @Autowired private VacXinRepository vacXinRepository;

    private UUID typeId;
    private UUID supplierId;

    @BeforeEach
    void setup() {
        // Khởi tạo Master Data trong H2 Database
        LoaiVacXin loai = loaiVacXinRepository.save(LoaiVacXin.builder().tenLoaiVacXin("Vắc-xin mRNA").build());
        NhaCungCap ncc = nhaCungCapRepository.save(NhaCungCap.builder().tenNhaCungCap("Pfizer Ltd").build());

        typeId = loai.getMaLoaiVacXin();
        supplierId = ncc.getMaNhaCungCap();
    }

    @Test
    @WithMockUser(authorities = "Quản lý kho")
    @DisplayName("Integration: Nhập kho vắc-xin mới và tự động sinh hóa đơn tài chính")
    void importVaccine_Integration_Success() throws Exception {
        VaccineImportRequest request = VaccineImportRequest.builder()
                .tenVacXin("Moderna-2026")
                .maLoaiVacXin(typeId)
                .soLo("MDRN-99")
                .soLuong(200)
                .giaNhap(new BigDecimal("120000"))
                .donGia(new BigDecimal("180000"))
                .maNhaCungCap(supplierId)
                .ngayNhan(LocalDate.now())
                .hanSuDung(LocalDate.now().plusYears(1))
                .build();

        mockMvc.perform(post("/api/v1/inventory/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.soLo").value("MDRN-99"));

        // VERIFY DATABASE THẬT
        // 1. Kiểm tra lô hàng đã lưu
        assertThat(loVacXinRepository.existsBySoLo("MDRN-99")).isTrue();

        // 2. Kiểm tra danh mục vắc-xin tự tạo
        assertThat(vacXinRepository.findByTenVacXin("Moderna-2026")).isPresent();

        // 3. Kiểm tra Hóa đơn nhập hàng (Tổng tiền = 200 * 120,000 = 24,000,000)
        var listHoaDon = hoaDonRepository.findAll();
        assertThat(listHoaDon).hasSize(1);
        assertThat(listHoaDon.get(0).getTongTien().compareTo(new BigDecimal("24000000"))).isEqualTo(0);
    }

    @Test
    @WithMockUser(authorities = "Quản lý kho")
    @DisplayName("Integration: Xuất kho và kiểm tra trừ tồn thực tế")
    void exportVaccine_Integration_Success() throws Exception {
        // GIVEN: Tạo sẵn 1 lô hàng 100 liều
        // (Sử dụng trực tiếp repository để chuẩn bị dữ liệu nhanh)
        var vaccine = vacXinRepository.save(VacXin.builder().tenVacXin("Astra").donGia(new BigDecimal("100")).build());
        var batch = loVacXinRepository.save(LoVacXin.builder()
                .soLo("TEST-EXP")
                .soLuong(100)
                .vacXin(vaccine)
                .tinhTrang("Còn")
                .build());

        VaccineExportRequest exportReq = VaccineExportRequest.builder()
                .maLo(batch.getMaLo())
                .soLuongXuat(40)
                .noiNhan("Phòng 01")
                .build();

        // WHEN
        mockMvc.perform(post("/api/v1/inventory/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exportReq)))
                .andExpect(status().isCreated());

        // THEN: Kiểm tra trong DB còn lại đúng 60 liều
        var updatedBatch = loVacXinRepository.findById(batch.getMaLo()).get();
        assertThat(updatedBatch.getSoLuong()).isEqualTo(60);
    }
}