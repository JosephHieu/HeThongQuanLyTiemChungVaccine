package com.josephhieu.vaccinebackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cấu trúc dữ liệu phản hồi chuẩn cho toàn bộ API hệ thống.
 * @param <T> Kiểu dữ liệu của kết quả trả về.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Không hiển thị trường null
public class ApiResponse<T> {
    @Builder.Default
    private int code = 1000; // 1000 là thành công
    private String message;
    private T result;
}
