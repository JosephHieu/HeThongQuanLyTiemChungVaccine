package com.josephhieu.vaccinebackend.modules.finance.service.impl;

import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.*;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.finance.service.FinanceService;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoaiVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceServiceImpl implements FinanceService {

    private final VacXinRepository vacXinRepository;
    private final LoaiVacXinRepository loaiVacXinRepository;
    private final LoVacXinRepository loVacXinRepository;
    private final HoaDonRepository hoaDonRepository;
    // Cập nhật: Xóa hoSoBenhAnRepository vì đã chuyển logic sang hoaDonRepository

    private static final String TYPE_XUAT = "XUAT";
    private static final String TYPE_NHAP = "NHAP";

    // =========================================================================
    // PHÂN HỆ 1: QUẢN LÝ VẮC-XIN
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<VacXin> vacXinPage = vacXinRepository.findAllByKeyword(keyword, pageable);

        List<VaccineFullResponse> data = vacXinPage.getContent().stream()
                .map(this::mapToFullResponse)
                .toList();

        return PageResponse.<VaccineFullResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(vacXinPage.getTotalPages())
                .totalElements(vacXinPage.getTotalElements())
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public VaccineFullResponse createVaccine(VaccineFullRequest request) {
        LoaiVacXin loai = loaiVacXinRepository.findById(request.getMaLoaiVacXin())
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        VacXin newVacXin = VacXin.builder()
                .tenVacXin(request.getTenVacXin())
                .loaiVacXin(loai)
                .hanSuDung(request.getHanSuDung())
                .hamLuong(request.getHamLuong())
                .phongNguaBenh(request.getPhongNguaBenh())
                .doTuoiTiemChung(request.getDoTuoiTiemChung())
                .donGia(request.getDonGia())
                .dieuKienBaoQuan(request.getDieuKienBaoQuan())
                .build();

        return mapToFullResponse(vacXinRepository.save(newVacXin));
    }

    @Override
    @Transactional
    public VaccineFullResponse updateVaccine(UUID id, VaccineFullRequest request) {
        VacXin vacXin = vacXinRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        LoaiVacXin loai = loaiVacXinRepository.findById(request.getMaLoaiVacXin())
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        vacXin.setTenVacXin(request.getTenVacXin());
        vacXin.setLoaiVacXin(loai);
        vacXin.setHanSuDung(request.getHanSuDung());
        vacXin.setHamLuong(request.getHamLuong());
        vacXin.setPhongNguaBenh(request.getPhongNguaBenh());
        vacXin.setDoTuoiTiemChung(request.getDoTuoiTiemChung());
        vacXin.setDonGia(request.getDonGia());
        vacXin.setDieuKienBaoQuan(request.getDieuKienBaoQuan());

        return mapToFullResponse(vacXinRepository.save(vacXin));
    }

    @Override
    @Transactional
    public void deleteVaccine(UUID id) {
        if (!vacXinRepository.existsById(id)) throw new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND);
        if (loVacXinRepository.existsByVacXin_MaVacXin(id)) throw new AppException(ErrorCode.INVALID_INFO);
        vacXinRepository.deleteById(id);
    }

    @Override
    public VaccineFullResponse getVaccineDetail(UUID id) {
        return vacXinRepository.findById(id).map(this::mapToFullResponse)
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));
    }

    // =========================================================================
    // PHÂN HỆ 2: GIAO DỊCH KHÁCH HÀNG
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerTransactionResponse> getCustomerTransactions(
            int page, int size, String search, String startDate, String endDate) {

        Pageable pageable = PageRequest.of(page - 1, size);
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        Page<CustomerTransactionResponse> result = hoaDonRepository.findCustomerTransactions(search, start, end, pageable);

        return PageResponse.<CustomerTransactionResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .data(result.getContent())
                .build();
    }

    @Override
    @Transactional
    public void confirmPayment(UUID maHoaDon, String phuongThucThanhToan) {
        HoaDon hd = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        if (!TYPE_XUAT.equalsIgnoreCase(hd.getLoaiHoaDon())) throw new AppException(ErrorCode.INVOICE_TYPE_MISMATCH);
        if (hd.getTrangThai() == 1) throw new AppException(ErrorCode.INVOICE_ALREADY_PAID);
        if (hd.getTrangThai() == 2) throw new AppException(ErrorCode.INVOICE_CANCELLED);

        hd.setTrangThai(1);
        hd.setPhuongThucThanhToan(phuongThucThanhToan);
        hd.setNgayTao(LocalDateTime.now());
        hoaDonRepository.save(hd);
    }

    // =========================================================================
    // PHÂN HỆ 3: GIAO DỊCH NHÀ CUNG CẤP (CẬP NHẬT MỚI)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SupplierTransactionResponse> getSupplierTransactions(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ngayTao").descending());

        // SỬA: Gọi hàm Repository trả về DTO thay vì Entity
        Page<SupplierTransactionResponse> result = hoaDonRepository.findSupplierTransactions(search, pageable);

        return PageResponse.<SupplierTransactionResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .data(result.getContent())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDon getSupplierTransactionDetail(UUID maHoaDon) {
        return hoaDonRepository.findById(maHoaDon)
                .filter(hd -> TYPE_NHAP.equalsIgnoreCase(hd.getLoaiHoaDon()))
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
    }

    @Override
    @Transactional
    public void confirmSupplierPayment(UUID maHoaDon, String phuongThuc) {
        HoaDon hd = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        if (!TYPE_NHAP.equalsIgnoreCase(hd.getLoaiHoaDon())) throw new AppException(ErrorCode.INVOICE_TYPE_MISMATCH);
        if (hd.getTrangThai() == 1) throw new AppException(ErrorCode.INVOICE_ALREADY_PAID);

        hd.setTrangThai(1);
        hd.setPhuongThucThanhToan(phuongThuc);
        hd.setNgayTao(LocalDateTime.now());
        hoaDonRepository.save(hd);
    }

    @Override
    @Transactional
    public void cancelTransaction(UUID maHoaDon) {
        HoaDon hd = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        if (hd.getTrangThai() == 1) throw new AppException(ErrorCode.CANNOT_CANCEL);
        hd.setTrangThai(2);
        hoaDonRepository.save(hd);
    }

    // =========================================================================
    // PHÂN HỆ 4: TỔNG QUAN & THỐNG KÊ
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public FinanceSummaryResponse getFinanceSummary() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        BigDecimal revenue = hoaDonRepository.sumRevenueByPeriod(start, end);
        // SỬA: Chỉ đếm hóa đơn chờ của khách hàng
        long pending = hoaDonRepository.countByTrangThaiAndLoaiHoaDon(0, TYPE_XUAT);
        BigDecimal inventoryValue = calculateTotalInventoryValue();

        return FinanceSummaryResponse.builder()
                .totalRevenueToday(revenue != null ? revenue : BigDecimal.ZERO)
                .pendingInvoiceCount(pending)
                .inventoryValue(inventoryValue)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierSummaryResponse getSupplierSummary() {
        // Lấy ngày đầu tháng này
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        // 1. Tính tổng chi (Hóa đơn NHAP đã thanh toán - trạng thái 1)
        BigDecimal totalSpending = hoaDonRepository.sumSpendingByPeriod(startOfMonth, now);

        // 2. Đếm số hóa đơn chưa thanh toán cho NCC (Công nợ - trạng thái 0)
        long pendingInvoices = hoaDonRepository.countByTrangThaiAndLoaiHoaDon(0, "NHAP");

        return SupplierSummaryResponse.builder()
                .totalSpendingThisMonth(totalSpending != null ? totalSpending : BigDecimal.ZERO)
                .spendingTrend("+12%") // Bạn có thể viết logic tính % so với tháng trước ở đây
                .overdueInvoices(pendingInvoices)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalInventoryValue() {
        BigDecimal total = loVacXinRepository.getTotalInventoryValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    // --- HELPER MAPPING ---
    private VaccineFullResponse mapToFullResponse(VacXin v) {
        String currentStaff = SecurityContextHolder.getContext().getAuthentication().getName();
        return VaccineFullResponse.builder()
                .maVacXin(v.getMaVacXin())
                .tenVacXin(v.getTenVacXin())
                .maLoaiVacXin(v.getLoaiVacXin() != null ? v.getLoaiVacXin().getMaLoaiVacXin() : null)
                .tenLoaiVacXin(v.getLoaiVacXin() != null ? v.getLoaiVacXin().getTenLoaiVacXin() : "N/A")
                .hanSuDung(v.getHanSuDung())
                .hamLuong(v.getHamLuong())
                .phongNguaBenh(v.getPhongNguaBenh())
                .doTuoiTiemChung(v.getDoTuoiTiemChung())
                .dieuKienBaoQuan(v.getDieuKienBaoQuan())
                .donGia(v.getDonGia())
                .ngayCapNhat(LocalDateTime.now())
                .nguoiCapNhat(currentStaff != null ? currentStaff : "System Admin")
                .build();
    }
}