package com.josephhieu.vaccinebackend.modules.medical.service;

import com.josephhieu.vaccinebackend.modules.medical.dto.request.EpidemicRequest;
import com.josephhieu.vaccinebackend.modules.medical.dto.response.EpidemicResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface cung cấp các nghiệp vụ liên quan đến quản lý và tra cứu dịch bệnh.
 * Phục vụ cho cả phân hệ Bệnh nhân (tra cứu) và phân hệ Nhân viên (nhập liệu).
 * * @author Joseph Hieu
 */
public interface EpidemicService {

    /**
     * Lấy danh sách toàn bộ tình hình dịch bệnh đã được khảo sát.
     * Dữ liệu được sắp xếp theo thời gian khảo sát mới nhất.
     * * @return Danh sách các đối tượng {@link EpidemicResponse} bao gồm thông tin gợi ý vắc-xin.
     */
    List<EpidemicResponse> getAllEpidemics();

    /**
     * Tìm kiếm tình hình dịch bệnh theo địa bàn hoặc khu vực.
     * * @param diaChi Từ khóa địa chỉ cần tìm kiếm (ví dụ: "Hà Nội", "Quận 1").
     * @return Danh sách dịch bệnh khớp với địa chỉ tìm kiếm.
     */
    List<EpidemicResponse> searchByLocation(String diachi);

    /**
     * Ghi nhận một đợt khảo sát dịch bệnh mới vào hệ thống.
     * Chức năng này chỉ dành cho Nhân viên y tế có thẩm quyền.
     * * @param request Thông tin chi tiết về đợt dịch bệnh từ {@link EpidemicRequest}.
     */
    void createEpidemic(EpidemicRequest request);

    EpidemicResponse getEpidemicById(UUID id);

    /**
     * Cập nhật thông tin một đợt dịch bệnh đã tồn tại.
     * @param id Mã định danh của dịch bệnh cần sửa.
     * @param request Dữ liệu cập nhật mới.
     * @return Thông tin dịch bệnh sau khi đã chỉnh sửa.
     */
    EpidemicResponse updateEpidemic(UUID id, EpidemicRequest request);

    /**
     * Xóa bỏ thông tin một đợt dịch bệnh khỏi hệ thống.
     * @param id Mã định danh của dịch bệnh cần xóa.
     */
    void deleteEpidemic(UUID id);

}
