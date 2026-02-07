package com.josephhieu.vaccinebackend.modules.medical.service.impl;

import com.josephhieu.vaccinebackend.common.exception.AppException;
import com.josephhieu.vaccinebackend.common.exception.ErrorCode;
import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.HighLevelFeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.HighLevelFeedbackResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.entity.PhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.repository.LoaiPhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.PhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.HighLevelFeedbackService;
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
 * Lớp triển khai các nghiệp vụ của {@link HighLevelFeedbackService}.
 * Xử lý logic nghiệp vụ về luồng dữ liệu phản hồi giữa Bệnh nhân và Administrator.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HighLevelFeedbackServiceImpl implements HighLevelFeedbackService {

    private final PhanHoiRepository phanHoiRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final LoaiPhanHoiRepository loaiPhanHoiRepository;

    @Override
    @Transactional
    public void sendFeedback(HighLevelFeedbackRequest request) {
        // 1. Xác định tài khoản đang thực hiện thao tác
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Truy xuất thông tin Bệnh nhân liên kết với tài khoản
        BenhNhan bn = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 3. Kiểm tra tính hợp lệ của loại phản hồi
        LoaiPhanHoi loai = loaiPhanHoiRepository.findById(request.getMaLoaiPhanHoi())
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_TYPE_NOT_FOUND));

        // 4. Khởi tạo và lưu trữ thực thể phản hồi
        PhanHoi ph = PhanHoi.builder()
                .benhNhan(bn)
                .loaiPhanHoi(loai)
                .noiDung(request.getNoiDung())
                .trangThai(0) // Mặc định: Mới gửi (New)
                .tenNhanVienPhuTrach("Administrator") // Administrator là người xử lý chính
                .build();

        phanHoiRepository.save(ph);
        log.info("Bệnh nhân {} đã gửi phản hồi cấp cao loại: {}", bn.getTenBenhNhan(), loai.getTenLoaiPhanHoi());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HighLevelFeedbackResponse> getMyFeedbackHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return phanHoiRepository.findByBenhNhan_TaiKhoan_TenDangNhapOrderByNgayTaoDesc(username).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HighLevelFeedbackResponse> getAllFeedbackForAdmin() {
        return phanHoiRepository.findAllByOrderByNgayTaoDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(UUID id, Integer status) {
        // Sửa ErrorCode thành FEEDBACK_NOT_FOUND
        PhanHoi ph = phanHoiRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        ph.setTrangThai(status);
        phanHoiRepository.save(ph);
        log.info("Administrator đã cập nhật trạng thái phản hồi {} sang mức: {}", id, status);
    }

    @Override
    @Transactional
    public void updateFeedback(UUID id, HighLevelFeedbackRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        PhanHoi ph = phanHoiRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        // 1. Kiểm tra quyền sở hữu: Phản hồi này có phải của người đang đăng nhập không?
        if (!ph.getBenhNhan().getTaiKhoan().getTenDangNhap().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 2. Kiểm tra trạng thái: Chỉ cho phép sửa khi chưa được xử lý (status = 0)
        if (ph.getTrangThai() != 0) {
            throw new AppException(ErrorCode.INVALID_REGISTRATION_STATUS); // Dùng mã lỗi "Trạng thái không hợp lệ"
        }

        // 3. Cập nhật
        LoaiPhanHoi loai = loaiPhanHoiRepository.findById(request.getMaLoaiPhanHoi())
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_TYPE_NOT_FOUND));

        ph.setLoaiPhanHoi(loai);
        ph.setNoiDung(request.getNoiDung());
        phanHoiRepository.save(ph);
        log.info("Bệnh nhân đã cập nhật lại phản hồi ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteMyFeedback(UUID id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PhanHoi ph = phanHoiRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        // Kiểm tra quyền sở hữu và trạng thái tương tự như Update
        if (!ph.getBenhNhan().getTaiKhoan().getTenDangNhap().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (ph.getTrangThai() != 0) {
            throw new AppException(ErrorCode.CANNOT_CANCEL); // Dùng mã lỗi "Không thể hủy"
        }

        phanHoiRepository.delete(ph);
        log.info("Bệnh nhân đã xóa phản hồi ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteFeedbackByAdmin(UUID id) {
        if (!phanHoiRepository.existsById(id)) {
            throw new AppException(ErrorCode.FEEDBACK_NOT_FOUND);
        }
        phanHoiRepository.deleteById(id);
        log.info("Administrator đã xóa phản hồi ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoaiPhanHoi> getFeedbackTypes() {
        return loaiPhanHoiRepository.findAll();
    }

    /**
     * Chuyển đổi từ Entity sang DTO Response để trả về phía giao diện.
     */
    private HighLevelFeedbackResponse mapToResponse(PhanHoi ph) {
        return HighLevelFeedbackResponse.builder()
                .maPhanHoi(ph.getMaPhanHoi())
                .tenLoaiPhanHoi(ph.getLoaiPhanHoi().getTenLoaiPhanHoi())
                .noiDung(ph.getNoiDung())
                .trangThai(ph.getTrangThai())
                .tenBenhNhan(ph.getBenhNhan().getTenBenhNhan())
                .sdtBenhNhan(ph.getBenhNhan().getSdt())
                .thoiGianGui(ph.getNgayTao() != null
                        ? ph.getNgayTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "N/A")
                .build();
    }
}