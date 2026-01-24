package com.josephhieu.vaccinebackend.modules.inventory.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineExportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineImportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.InventoryResponse;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.NhaCungCap;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoaiVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.NhaCungCapRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final LoVacXinRepository loVacXinRepository;
    private final VacXinRepository vacXinRepository;
    private final LoaiVacXinRepository loaiVacXinRepository;
    private final NhaCungCapRepository nhaCungCapRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getInventoryPage(String criteria, String search, Pageable pageable) {
        return loVacXinRepository.searchInventory(criteria, search, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public InventoryResponse importVaccine(VaccineImportRequest request) {

        // 1. Kiểm tra/Tạo danh mục Vắc-xin gốc (Master Data)
        VacXin vacXin = vacXinRepository.findByTenVacXin(request.getTenVacXin())
                .orElseGet(() -> createNewVaccineCategory(request));

        // 2. Kiểm tra Nhà cung cấp
        NhaCungCap ncc = nhaCungCapRepository.findById(request.getMaNhaCungCap())
                .orElseThrow(() -> new RuntimeException("Lỗi: Nhà cung cấp không tồn tại trong hệ thống."));

        // 3. Khởi tạo lô hàng mới (Transaction Record)
        LoVacXin loMoi = LoVacXin.builder()
                .vacXin(vacXin)
                .nhaCungCap(ncc)
                .soLuong(request.getSoLuong())
                .ngayNhan(request.getNgayNhan())
                .nuocSanXuat(request.getNuocSanXuat())
                .giayPhep(request.getGiayPhep())
                .tinhTrang(request.getSoLuong() > 0 ? "Còn" : "Hết")
                .ghiChu(request.getGhiChu())
                .build();

        return mapToResponse(loVacXinRepository.save(loMoi));
    }

    @Override
    @Transactional
    public InventoryResponse exportVaccine(VaccineExportRequest request) {

        LoVacXin loHienTai = loVacXinRepository.findById(request.getMaLo())
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

        // Kiểm tra Null để tránh NPE trước khi so sánh
        Integer soLuongHienTai = loHienTai.getSoLuong() != null ? loHienTai.getSoLuong() : 0;
        Integer soLuongXuat = request.getSoLuongXuat() != null ? request.getSoLuongXuat() : 0;

        if (soLuongHienTai < soLuongXuat) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        // Cập nhật
        int soLuongMoi = soLuongHienTai - soLuongXuat;
        loHienTai.setSoLuong(soLuongMoi);
        loHienTai.setTinhTrang(soLuongMoi > 0 ? "Còn" : "Hết");

        return mapToResponse(loVacXinRepository.save(loHienTai));
    }

    @Override
    public InventoryResponse getBatchDetail(UUID maLo) {
        return loVacXinRepository.findById(maLo)
                .map(this::mapToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    /**
     * Helper: Tạo mới danh mục vắc-xin khi nhập loại chưa có trong kho.
     */
    private VacXin createNewVaccineCategory(VaccineImportRequest request) {
        LoaiVacXin loai = loaiVacXinRepository.findById(request.getMaLoaiVacXin())
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        return vacXinRepository.save(VacXin.builder()
                .tenVacXin(request.getTenVacXin())
                .loaiVacXin(loai)
                .donGia(request.getDonGia())
                .hanSuDung(request.getHanSuDung())
                .hamLuong(request.getHamLuong())
                .dieuKienBaoQuan(request.getDieuKienBaoQuan())
                .doTuoiTiemChung(request.getDoTuoiTiemChung())
                .build());
    }

    /**
     * Helper: Chuyển đổi Entity sang DTO để trả về cho Frontend (Flattening).
     */
    private InventoryResponse mapToResponse(LoVacXin lo) {
        return InventoryResponse.builder()
                .maLo(lo.getMaLo())
                .tenVacXin(lo.getVacXin().getTenVacXin())
                .tenLoaiVacXin(lo.getVacXin().getLoaiVacXin().getTenLoaiVacXin())
                .soLuong(lo.getSoLuong())
                .hanSuDung(lo.getVacXin().getHanSuDung())
                .tinhTrang(lo.getTinhTrang())
                .nuocSanXuat(lo.getNuocSanXuat())
                .giayPhep(lo.getGiayPhep())
                .build();
    }
}
