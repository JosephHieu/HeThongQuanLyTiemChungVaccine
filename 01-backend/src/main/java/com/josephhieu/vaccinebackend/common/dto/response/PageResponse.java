package com.josephhieu.vaccinebackend.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Lớp dùng chung để trả về dữ liệu phân trang cho Frontend.
 * T: Kiểu dữ liệu của danh sách (ví dụ: UserResponse).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private int currentPage;    // Trang hiện tại (bắt đầu từ 1)
    private int totalPages;     // Tổng số trang có thể hiển thị
    private int pageSize;       // Số lượng bản ghi trên mỗi trang
    private long totalElements; // Tổng số bản ghi thực tế trong Database

    private List<T> data; // Danh sách dữ liệu thực tế
}
