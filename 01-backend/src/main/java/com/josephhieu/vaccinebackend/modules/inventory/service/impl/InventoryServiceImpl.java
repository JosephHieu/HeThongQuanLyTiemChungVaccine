package com.josephhieu.vaccinebackend.modules.inventory.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineExportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineImportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.InventoryResponse;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.VaccineExportResponse;
import com.josephhieu.vaccinebackend.modules.inventory.entity.*;
import com.josephhieu.vaccinebackend.modules.inventory.repository.*;
import com.josephhieu.vaccinebackend.modules.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final LoVacXinRepository loVacXinRepository;
    private final VacXinRepository vacXinRepository;
    private final LoaiVacXinRepository loaiVacXinRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final PhieuXuatRepository phieuXuatRepository;
    private final HoaDonRepository hoaDonRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getInventoryPage(String criteria, String search, Pageable pageable) {

        // Đảm bảo search không bị null để tránh lỗi Query
        String searchKey = (search == null) ? "" : search;

        return loVacXinRepository.searchInventory(criteria, searchKey, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public InventoryResponse importVaccine(VaccineImportRequest request) {

        // 1. Kiểm tra số lô trùng lặp
        if (loVacXinRepository.existsBySoLo(request.getSoLo())) {
            throw new AppException(ErrorCode.BATCH_ALREADY_EXISTS);
        }

        // 2. Kiểm tra/Tạo danh mục vắc-xin gốc
        VacXin vacXin = vacXinRepository.findByTenVacXin(request.getTenVacXin())
                .map(existingVacxin -> {
                    // Nếu đã tồn tại, hãy cập nhật các thông tin y tế mới từ form
                    existingVacxin.setPhongNguaBenh(request.getPhongNguaBenh());
                    existingVacxin.setDoTuoiTiemChung(request.getDoTuoiTiemChung());
                    existingVacxin.setHamLuong(request.getHamLuong());
                    return vacXinRepository.save(existingVacxin);
                })
                .orElseGet(() -> createNewVaccineCategory(request)); // Nếu chưa có thì mới tạo mới

        // 3. Kiểm tra Nhà cung cấp
        NhaCungCap ncc = nhaCungCapRepository.findById(request.getMaNhaCungCap())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        // 4. --- BƯỚC QUAN TRỌNG: TẠO HÓA ĐƠN TÀI CHÍNH ---
        BigDecimal tongTienHienTai = request.getGiaNhap().multiply(BigDecimal.valueOf(request.getSoLuong()));

        HoaDon hoaDonNhap = HoaDon.builder()
                .tongTien(tongTienHienTai)
                .ngayTao(LocalDateTime.now())
                .trangThai(0) // Chờ thanh toán cho NCC
                .loaiHoaDon("Nhap")
                .build();

        hoaDonNhap = hoaDonRepository.save(hoaDonNhap);

        // 3. Khởi tạo lô hàng mới (Transaction Record)
        LoVacXin loMoi = LoVacXin.builder()
                .vacXin(vacXin)
                .nhaCungCap(ncc)
                .hoaDon(hoaDonNhap)
                .soLo(request.getSoLo())
                .soLuong(request.getSoLuong())
                .ngayNhan(request.getNgayNhan())
                .nuocSanXuat(request.getNuocSanXuat())
                .giayPhep(request.getGiayPhep())
                .tinhTrang(request.getSoLuong() > 0 ? "Còn" : "Hết")
                .ghiChu(request.getGhiChu())
                .giaNhap(request.getGiaNhap())
                .build();

        return mapToResponse(loVacXinRepository.save(loMoi));
    }

    @Override
    @Transactional
    public VaccineExportResponse exportVaccine(VaccineExportRequest request) {

        // 1. Tìm lô vắc-xin hiện tại
        LoVacXin loHienTai = loVacXinRepository.findById(request.getMaLo())
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

        // 2. Kiểm tra tồn kho
        int soLuongHienTai = loHienTai.getSoLuong() != null ? loHienTai.getSoLuong() : 0;
        if (soLuongHienTai < request.getSoLuongXuat()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        // 3. Cập nhật số lượng và tình trạng lô vắc-xin
        int soLuongMoi = soLuongHienTai - request.getSoLuongXuat();
        loHienTai.setSoLuong(soLuongMoi);
        loHienTai.setTinhTrang(soLuongMoi > 0 ? "Còn" : "Hết");
        loVacXinRepository.save(loHienTai);

        // 4. Tạo phiếu xuất mới
        String soPhieuMoi = "PX-" + System.currentTimeMillis() / 1000;

        PhieuXuat phieu = PhieuXuat.builder()
                .soPhieuXuat(soPhieuMoi)
                .loVacXin(loHienTai)
                .soLuongXuat(request.getSoLuongXuat())
                .noiNhan(request.getNoiNhan())
                .ghiChu(request.getGhiChu())
                .maNhanVien(request.getMaNhanVien())
                .build();

        PhieuXuat savedPhieu = phieuXuatRepository.save(phieu);

        // 5. Trả về Response DTO cho Frontend
        return VaccineExportResponse.builder()
                .soPhieuXuat(savedPhieu.getSoPhieuXuat())
                .tenVacXin(loHienTai.getVacXin().getTenVacXin())
                .soLoThucTe(loHienTai.getSoLo())
                .soLuongDaXuat(savedPhieu.getSoLuongXuat())
                .soLuongConLaiTrongKho(soLuongMoi)
                .ngayXuat(savedPhieu.getNgayXuat())
                .noiNhan(savedPhieu.getNoiNhan())
                .build();
    }

    @Override
    public InventoryResponse getBatchDetail(UUID maLo) {
        return loVacXinRepository.findById(maLo)
                .map(this::mapToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    @Override
    public List<NhaCungCap> getAllSuppliers() {
        return nhaCungCapRepository.findAll();
    }

    @Override
    public List<LoaiVacXin> getAllVaccineTypes() {
        return loaiVacXinRepository.findAll();
    }

    @Override
    public Long getTotalDoses() {

        Long total = loVacXinRepository.getTotalDoses();
        return total != null ? total : 0L;
    }

    @Override
    public Page<VaccineExportResponse> getExportHistory(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<PhieuXuat> pages;
        if (start != null && end != null) {
            pages = phieuXuatRepository.findByNgayXuatBetween(start, end, pageable);
        } else {
            pages = phieuXuatRepository.findAll(pageable);
        }
        return pages.map(this::mapToExportResponse);
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
                .phongNguaBenh(request.getPhongNguaBenh())
                .build());
    }



    /**
     * Helper: Chuyển đổi Entity sang DTO để trả về cho Frontend (Flattening).
     */
    private InventoryResponse mapToResponse(LoVacXin lo) {
        return InventoryResponse.builder()
                .maLo(lo.getMaLo())
                .soLo(lo.getSoLo())
                .tenVacXin(lo.getVacXin().getTenVacXin())
                .tenLoaiVacXin(lo.getVacXin().getLoaiVacXin().getTenLoaiVacXin())
                .doTuoiTiemChung(lo.getVacXin().getDoTuoiTiemChung())
                .hamLuong(lo.getVacXin().getHamLuong())
                .phongNguaBenh(lo.getVacXin().getPhongNguaBenh())
                .soLuong(lo.getSoLuong())
                .hanSuDung(lo.getVacXin().getHanSuDung())
                .dieuKienBaoQuan(lo.getVacXin().getDieuKienBaoQuan())
                .tinhTrang(lo.getTinhTrang())
                .nuocSanXuat(lo.getNuocSanXuat())
                .giayPhep(lo.getGiayPhep())
                .giaNhap(lo.getGiaNhap())
                .donGia(lo.getVacXin().getDonGia())
                .build();
    }

    private VaccineExportResponse mapToExportResponse(PhieuXuat phieu) {

        return VaccineExportResponse.builder()
                .maPhieuXuat(phieu.getMaPhieuXuat())
                .soPhieuXuat(phieu.getSoPhieuXuat())
                .tenVacXin(phieu.getLoVacXin().getVacXin().getTenVacXin())
                .soLoThucTe(phieu.getLoVacXin().getSoLo())
                .soLuongDaXuat(phieu.getSoLuongXuat())
                .ngayXuat(phieu.getNgayXuat())
                .noiNhan(phieu.getNoiNhan())
                .ghiChu(phieu.getGhiChu())
                // Ở danh sách lịch sử, ta có thể không cần hiện số lượng còn lại trong kho hiện tại
                .soLuongConLaiTrongKho(phieu.getLoVacXin().getSoLuong())
                .build();
    }
}
