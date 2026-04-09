package com.josephhieu.vaccinebackend.modules.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephhieu.vaccinebackend.modules.identity.dto.request.UserCreationRequest;
import com.josephhieu.vaccinebackend.modules.identity.entity.PhanQuyen;
import com.josephhieu.vaccinebackend.modules.identity.repository.*;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Rollback dữ liệu sau mỗi test case
public class UserIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private TaiKhoanRepository taiKhoanRepository;
    @Autowired private PhanQuyenRepository phanQuyenRepository;
    @Autowired private BenhNhanRepository benhNhanRepository;
    @Autowired private NhanVienRepository nhanVienRepository;
    @Autowired private ChiTietPhanQuyenRepository chiTietPhanQuyenRepository;

    private UUID adminRoleId;
    private UUID patientRoleId;

    @BeforeEach
    void setup() {
        // Khởi tạo các quyền cơ bản trong database H2
        PhanQuyen admin = phanQuyenRepository.save(PhanQuyen.builder().tenQuyen("Administrator").build());
        PhanQuyen patient = phanQuyenRepository.save(PhanQuyen.builder().tenQuyen("Normal User Account").build());

        adminRoleId = admin.getMaQuyen();
        patientRoleId = patient.getMaQuyen();
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("Integration: Tạo nhân viên y tế - Dữ liệu phải lưu đồng thời vào 3 bảng")
    void createStaff_IntegrationFlow() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .tenDangNhap("staff_2026")
                .matKhau("123456")
                .hoTen("Nguyen Van A")
                .maQuyen(adminRoleId.toString())
                .cmnd("123456789")
                .noiO("Ha Noi")
                .build();

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Kiểm tra bảng TAIKHOAN
        assertTrue(taiKhoanRepository.existsByTenDangNhap("staff_2026"));

        // Kiểm tra bảng NHANVIEN (Vì không phải Normal User Account nên vào bảng NhanVien)
        var nhanVien = nhanVienRepository.findAll().stream()
                .filter(nv -> nv.getTenNhanVien().equals("Nguyen Van A"))
                .findFirst();
        assertTrue(nhanVien.isPresent());

        // Kiểm tra bảng CHITIETPHANQUYEN
        assertEquals(1, chiTietPhanQuyenRepository.count());
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("Integration: Tạo bệnh nhân - Dữ liệu y tế phải nằm trong bảng BENHNHAN")
    void createPatient_IntegrationFlow() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .tenDangNhap("patient_hieu")
                .matKhau("123456")
                .hoTen("Joseph Patient")
                .maQuyen(patientRoleId.toString())
                .cmnd("987654321")
                .noiO("TP HCM")
                .ngaySinh(LocalDate.of(1995, 1, 1))
                .gioiTinh("Nam")
                .sdt("0909123456")
                .build();

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Kiểm tra bảng BENHNHAN
        var benhNhan = benhNhanRepository.findAll().stream()
                .filter(bn -> bn.getSdt().equals("0909123456"))
                .findFirst();

        assertTrue(benhNhan.isPresent());
        assertEquals("Joseph Patient", benhNhan.get().getTenBenhNhan());
        assertEquals("Nam", benhNhan.get().getGioiTinh());
    }

    @Test
    @WithMockUser(authorities = "Administrator")
    @DisplayName("Integration: Transaction Rollback - Nếu lỗi MaQuyen thì không lưu bất cứ gì")
    void transactionRollback_Test() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .tenDangNhap("error_user")
                .matKhau("123456")
                .hoTen("Error User")
                .cmnd("123456789")
                .noiO("Hanoi")
                .maQuyen(UUID.randomUUID().toString()) // Quyền này KHÔNG có trong DB
                .build();

        // WHEN: Thực hiện gọi API
        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        assertFalse(taiKhoanRepository.existsByTenDangNhap("error_user"),
                "Tài khoản không được phép tồn tại nếu quy trình bị lỗi giữa chừng");
    }
}