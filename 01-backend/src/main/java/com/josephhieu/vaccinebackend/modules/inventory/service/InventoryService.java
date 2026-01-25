package com.josephhieu.vaccinebackend.modules.inventory.service;

import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineExportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.request.VaccineImportRequest;
import com.josephhieu.vaccinebackend.modules.inventory.dto.response.InventoryResponse;
import com.josephhieu.vaccinebackend.modules.inventory.entity.LoaiVacXin;
import com.josephhieu.vaccinebackend.modules.inventory.entity.NhaCungCap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Interface định nghĩa các nghiệp vụ quản lý kho vắc-xin.
 * Bao gồm: Nhập kho, xuất kho, tìm kiếm và thống kê.
 * * @author Joseph Hieu
 */
public interface InventoryService {

    /**
     * Tìm kiếm và phân trang danh sách lô vắc-xin trong kho.
     * @param criteria Tiêu chí tìm kiếm (name, type, origin)
     * @param search Từ khóa tìm kiếm
     * @param pageable Đối số phân trang
     * @return Trang danh sách InventoryResponse
     */
    Page<InventoryResponse> getInventoryPage(String criteria, String search, Pageable pageable);

    /**
     * Thực hiện nghiệp vụ nhập kho lô vắc-xin mới.
     * Nếu vắc-xin chưa tồn tại trong danh mục, hệ thống sẽ tự động khởi tạo.
     * @param request Dữ liệu nhập kho từ Frontend
     * @return Thông tin lô hàng vừa nhập
     */
    InventoryResponse importVaccine(VaccineImportRequest request);

    /**
     * Thực hiện nghiệp vụ xuất kho điều phối vắc-xin.
     * Kiểm tra số lượng tồn và cập nhật trạng thái lô hàng.
     * @param request Dữ liệu xuất kho (Mã lô, số lượng)
     * @return Thông tin lô hàng sau khi xuất
     */
    InventoryResponse exportVaccine(VaccineExportRequest request);

    /**
     * Lấy thông tin chi tiết của một lô vắc-xin qua ID.
     * @param maLo ID định danh duy nhất (UUID)
     */
    InventoryResponse getBatchDetail(UUID maLo);

    /**
     * Lấy danh sách tất cả nhà cung cấp để hiển thị Dropdown.
     */
    List<NhaCungCap> getAllSuppliers();

    /**
     * Lấy danh sách tất cả loại vắc-xin để hiển thị Dropdown.
     */
    List<LoaiVacXin> getAllVaccineTypes();
}
