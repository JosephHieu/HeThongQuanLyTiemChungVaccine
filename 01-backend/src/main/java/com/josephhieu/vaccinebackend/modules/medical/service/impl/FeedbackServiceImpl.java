package com.josephhieu.vaccinebackend.modules.medical.service.impl;

import com.josephhieu.vaccinebackend.modules.identity.entity.BenhNhan;
import com.josephhieu.vaccinebackend.modules.identity.repository.BenhNhanRepository;
import com.josephhieu.vaccinebackend.modules.medical.dto.request.FeedbackRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.FeedbackResponse;
import com.josephhieu.vaccinebackend.modules.medical.entity.LoaiPhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.entity.PhanHoi;
import com.josephhieu.vaccinebackend.modules.medical.repository.LoaiPhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.repository.PhanHoiRepository;
import com.josephhieu.vaccinebackend.modules.medical.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.tomcat.util.http.FastHttpDateFormat.parseDate;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final PhanHoiRepository phanHoiRepository;
    private final LoaiPhanHoiRepository loaiPhanHoiRepository;
    private final BenhNhanRepository benhNhanRepository;

    @Override
    @Transactional
    public void sendFeedback(FeedbackRequest request) {

        // 1. Lấy Username từ Security Context (người dùng đang đăng nhập)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Tìm thông tin bệnh nhân
        BenhNhan benhNhan = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bệnh nhân!"));

        // 3. Lấy hoặc tạo LoaiPhanHoi mặc định
        LoaiPhanHoi loai = loaiPhanHoiRepository.findByTenLoaiPhanHoi("Phản hồi sau tiêm")
                .orElseGet(() -> loaiPhanHoiRepository.save(
                        LoaiPhanHoi.builder().tenLoaiPhanHoi("Phản hồi sau tiêm").build()
                ));

        // 4. Mapping DTO sang Entity & Lưu trữ
        PhanHoi phanHoi = PhanHoi.builder()
                .benhNhan(benhNhan)
                .loaiPhanHoi(loai)
                .tenVacXin(request.getTenVacXin())
                .tenNhanVienPhuTrach(request.getNhanVienPhuTrach())
                .noiDung(request.getNoiDung())
                .diaDiemTiem(request.getDiaDiemTiem())
                .thoiGianTiem(parseDate(request.getThoiGianTiem()))
                .trangThai(0) // 0: Mới gửi
                .build();

        phanHoiRepository.save(phanHoi);
    }

    @Override
    public List<FeedbackResponse> getMyFeedbacks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Lấy bệnh nhân hiện tại
        BenhNhan bn = benhNhanRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Truy vấn danh sách từ Repo (sử dụng method chúng ta đã tạo trước đó)
        return phanHoiRepository.findByBenhNhan_MaBenhNhanOrderByThoiGianTiemDesc(bn.getMaBenhNhan())
                .stream()
                .map(ph -> FeedbackResponse.builder()
                        .maPhanHoi(ph.getMaPhanHoi())
                        .tenVacXin(ph.getTenVacXin())
                        .noiDung(ph.getNoiDung())
                        .trangThai(ph.getTrangThai().toString()) // Chuyển sang String để hiển thị
                        .thoiGianTiem(ph.getThoiGianTiem().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacks() {
        return List.of();
    }


    // Hàm phụ trợ để parse ngày từ chuỗi "dd/MM/yyyy HH:mm" của Frontend
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return LocalDate.now();
        try {
            // Cắt lấy phần ngày dd/MM/yyyy bỏ phần giờ
            String cleanDate = dateStr.split(" ")[0];
            return LocalDate.parse(cleanDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
