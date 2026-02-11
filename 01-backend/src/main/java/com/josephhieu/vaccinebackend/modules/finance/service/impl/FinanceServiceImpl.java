package com.josephhieu.vaccinebackend.modules.finance.service.impl;

import com.josephhieu.vaccinebackend.common.dto.response.PageResponse;
import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.finance.dto.request.VaccineFullRequest;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.CustomerTransactionResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.FinanceSummaryResponse;
import com.josephhieu.vaccinebackend.modules.finance.dto.response.VaccineFullResponse;
import com.josephhieu.vaccinebackend.modules.finance.entity.HoaDon;
import com.josephhieu.vaccinebackend.modules.finance.repository.HoaDonRepository;
import com.josephhieu.vaccinebackend.modules.finance.service.FinanceService;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.VacXin;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.LoaiVacXinRepository;
import com.josephhieu.vaccinebackend.modules.inventory.repository.VacXinRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.HoSoBenhAnRepository;
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
    private final HoSoBenhAnRepository hoSoBenhAnRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<VacXin> vacXinPage = vacXinRepository.findAll(pageable);

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

        log.info("Thêm mới vắc-xin vào danh mục: {}", request.getTenVacXin());
        return mapToFullResponse(vacXinRepository.save(newVacXin));
    }

    @Override
    @Transactional
    public VaccineFullResponse updateVaccine(UUID id, VaccineFullRequest request) {
        VacXin vacXin = vacXinRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        LoaiVacXin loai = loaiVacXinRepository.findById(request.getMaLoaiVacXin())
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));

        // Mapping dữ liệu từ Request sang Entity
        vacXin.setTenVacXin(request.getTenVacXin());
        vacXin.setLoaiVacXin(loai);
        vacXin.setHanSuDung(request.getHanSuDung());
        vacXin.setHamLuong(request.getHamLuong());
        vacXin.setPhongNguaBenh(request.getPhongNguaBenh());
        vacXin.setDoTuoiTiemChung(request.getDoTuoiTiemChung());
        vacXin.setDonGia(request.getDonGia());
        vacXin.setDieuKienBaoQuan(request.getDieuKienBaoQuan());

        log.info("Cập nhật toàn diện vắc-xin ID: {}", id);
        return mapToFullResponse(vacXinRepository.save(vacXin));
    }

    @Override
    @Transactional
    public void deleteVaccine(UUID id) {
        if (!vacXinRepository.existsById(id)) {
            throw new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND);
        }

        // Ràng buộc nghiệp vụ: Không xóa nếu đã có lô vắc-xin (tránh mồ côi dữ liệu)
        if (loVacXinRepository.existsByVacXin_MaVacXin(id)) {
            throw new AppException(ErrorCode.INVALID_INFO);
        }

        vacXinRepository.deleteById(id);
        log.warn("Đã xóa vắc-xin ID: {} khỏi hệ thống", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalInventoryValue() {
        BigDecimal total = loVacXinRepository.getTotalInventoryValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public VaccineFullResponse getVaccineDetail(UUID id) {
        return vacXinRepository.findById(id)
                .map(this::mapToFullResponse)
                .orElseThrow(() -> new AppException(ErrorCode.VACCINE_TYPE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VaccineFullResponse> getVaccineManagementList(int page, int size, String keyword) {
        // Spring Data JPA dùng page bắt đầu từ 0
        Pageable pageable = PageRequest.of(page - 1, size);

        // Gọi Repository mới với keyword
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
    @Transactional(readOnly = true)
    public PageResponse<CustomerTransactionResponse> getCustomerTransactions(
            int page, int size, String search, String startDate, String endDate) {

        Pageable pageable = PageRequest.of(page - 1, size);

        // Cập nhật cách parse ngày để an toàn hơn
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        // THAY ĐỔI QUAN TRỌNG: Gọi hoaDonRepository thay vì hoSoBenhAnRepository
        Page<CustomerTransactionResponse> result = hoaDonRepository.findCustomerTransactions(
                search, start, end, pageable);

        return PageResponse.<CustomerTransactionResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .data(result.getContent())
                .build();
    }

    @Override
    public void confirmPayment(UUID maHoaDon, String phuongThucThanhToan) {

        // 1. Kiểm tra tồn tại
        HoaDon hd = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND)); // Invoice not found

        // 2. Kiểm tra loại hóa đơn
        if (!"Xuat".equalsIgnoreCase(hd.getLoaiHoaDon())) {
            throw new AppException(ErrorCode.INVOICE_TYPE_MISMATCH);
        }

        // 3. Kiểm tra trạng thái: Đã hủy thì không được thu tiền
        if (hd.getTrangThai() == 2) {
            throw new AppException(ErrorCode.INVOICE_CANCELLED);
        }

        // 4. Kiểm tra trạng thái: Đã thanh toán rồi thì không thu nữa
        if (hd.getTrangThai() == 1) {
            throw new AppException(ErrorCode.INVOICE_ALREADY_PAID);
        }

        // 5. Kiểm tra phương thức thanh toán truyền lên
        if (phuongThucThanhToan == null || phuongThucThanhToan.isBlank()) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_INVALID);
        }

        hd.setTrangThai(1);
        hd.setPhuongThucThanhToan(phuongThucThanhToan);
        hd.setNgayTao(LocalDateTime.now());

        hoaDonRepository.save(hd);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<HoaDon> getSupplierTransactions(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ngayTao").descending());

        Page<HoaDon> result = hoaDonRepository.findSupplierTransactions(search, pageable);

        return PageResponse.<HoaDon>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .data(result.getContent())
                .build();
    }

    @Override
    @Transactional
    public void cancelTransaction(UUID maHoaDon) {

        HoaDon hd = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        // Ràng buộc: Đã thanh toán thì không được hủy theo cách này (phải hoàn tiền)
        if (hd.getTrangThai() == 1) {
            throw new AppException(ErrorCode.CANNOT_CANCEL);
        }

        hd.setTrangThai(2); // 2: Đã hủy
        hoaDonRepository.save(hd);

        log.warn("Đã hủy hóa đơn ID: {}", maHoaDon);
    }

    @Override
    @Transactional(readOnly = true)
    public FinanceSummaryResponse getFinanceSummary() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        // 1. Tính doanh thu trong ngày
        BigDecimal todayRevenue = hoaDonRepository.sumRevenueByPeriod(startOfDay, endOfDay);

        // 2. Đếm số hóa đơn chưa thanh toán (Trạng thái 0)
        long pendingCount = hoaDonRepository.countByTrangThai(0);

        // 3. Lấy giá trị tồn kho (Đã có hàm viết sẵn bên trên)
        BigDecimal inventoryValue = calculateTotalInventoryValue();

        return FinanceSummaryResponse.builder()
                .totalRevenueToday(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .pendingInvoiceCount(pendingCount)
                .inventoryValue(inventoryValue)
                .build();
    }

    /**
     * Chuyển đổi Entity VacXin sang VaccineFullResponse DTO.
     * Tự động lấy danh tính nhân viên đang thao tác để gắn vào Audit Info.
     */
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
                .ngayCapNhat(java.time.LocalDateTime.now())
                .nguoiCapNhat(currentStaff != null ? currentStaff : "System Admin")
                .build();
    }
}