package com.josephhieu.vaccinebackend.modules.medical.service.impl;

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
import com.josephhieu.vaccinebackend.modules.medical.service.EpidemicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Lớp triển khai các nghiệp vụ của {@link EpidemicService}.
 * Xử lý logic mapping dữ liệu và kết nối thông tin giữa dịch bệnh và vắc-xin.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EpidemicServiceImpl implements EpidemicService {

    private final DichBenhRepository dichBenhRepository;
    private final VacXinRepository vacXinRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EpidemicResponse> getAllEpidemics() {

        return dichBenhRepository.findAllByOrderByThoiDiemKhaoSatDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EpidemicResponse> searchByLocation(String diachi) {
        return dichBenhRepository.findByDiaChiContainingIgnoreCaseOrderByThoiDiemKhaoSatDesc(diachi).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createEpidemic(EpidemicRequest request) {

        // 1. Xác định nhân viên đang thực hiện thao tác từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        NhanVien nv = nhanVienRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Xây dựng thực thể DichBenh từ Request
        DichBenh dichBenh = DichBenh.builder()
                .nhanVien(nv)
                .tenDichBenh(request.getTenDichBenh())
                .duongLayNhiem(request.getDuongLayNhiem())
                .tacHaiSucKhoe(request.getTacHaiSucKhoe())
                .soNguoiBiNhiem(request.getSoNguoiBiNhiem())
                .diaChi(request.getDiaChi())
                .ghiChu(request.getGhiChu())
                .thoiDiemKhaoSat(request.getThoiDiemKhaoSat())
                .build();

        dichBenhRepository.save(dichBenh);
        log.info("Nhân viên {} đã cập nhật dịch bệnh mới: {}",username, request.getTenDichBenh());
    }

    @Override
    @Transactional(readOnly = true)
    public EpidemicResponse getEpidemicById(UUID id) {
        return dichBenhRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.EPIDEMIC_NOT_FOUND));
    }

    @Override
    @Transactional
    public EpidemicResponse updateEpidemic(UUID id, EpidemicRequest request) {

        // 1. Tìm bản ghi hiện có
        DichBenh dichBenh = dichBenhRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EPIDEMIC_NOT_FOUND));

        // 2. Cập nhật thông tin mới từ request
        dichBenh.setTenDichBenh(request.getTenDichBenh());
        dichBenh.setDuongLayNhiem(request.getDuongLayNhiem());
        dichBenh.setTacHaiSucKhoe(request.getTacHaiSucKhoe());
        dichBenh.setSoNguoiBiNhiem(request.getSoNguoiBiNhiem());
        dichBenh.setDiaChi(request.getDiaChi());
        dichBenh.setGhiChu(request.getGhiChu());
        dichBenh.setThoiDiemKhaoSat(request.getThoiDiemKhaoSat());

        // 3. Lưu và trả về response
        DichBenh updated = dichBenhRepository.save(dichBenh);
        log.info("Đã cập nhật thông tin dịch bệnh có ID: {}", id);
        return convertToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteEpidemic(UUID id) {

        // Kiểm tra tồn tại trước khi xóa
        if (!dichBenhRepository.existsById(id)) {
            throw new AppException(ErrorCode.EPIDEMIC_NOT_FOUND);
        }
        dichBenhRepository.deleteById(id);
        log.warn("Nhân viên y tế đã xóa bản ghi dịch bênh có ID: {}", id);
    }


    /**
     * Chuyển đổi từ Entity sang DTO và thực hiện logic gợi ý vắc-xin tương ứng.
     * * @param db Thực thể dịch bệnh cần chuyển đổi.
     * @return Đối tượng Response đã được làm giàu dữ liệu vắc-xin.
     */
    private EpidemicResponse convertToResponse(DichBenh db) {
        // Logic: Tìm vắc-xin có cột 'phongNguaBenh' chứa tên của dịch bệnh này
        List<String> suggestedVaccines = vacXinRepository
                .findByPhongNguaBenhContainingIgnoreCase(db.getTenDichBenh())
                .stream()
                .map(VacXin::getTenVacXin)
                .distinct()
                .collect(Collectors.toList());

        return EpidemicResponse.builder()
                .maDichBenh(db.getMaDichBenh())
                .thoiDiemKhaoSat(db.getThoiDiemKhaoSat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .diaChi(db.getDiaChi())
                .tenDichBenh(db.getTenDichBenh())
                .soNguoiBiNhiem(db.getSoNguoiBiNhiem())
                .duongLayNhiem(db.getDuongLayNhiem())
                .tacHaiSucKhoe(db.getTacHaiSucKhoe())
                .ghiChu(db.getGhiChu())
                .vacXinGoiY(suggestedVaccines)
                .tenNhanVienKhaoSat(db.getNhanVien() != null ? db.getNhanVien().getTenNhanVien() : "N/A")
                .build();
    }
}
