package com.josephhieu.vaccinebackend.common.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Static Factory Methods để code ở Controller "sạch" hơn
    public static <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> success(T result, String message) {
        return ApiResponse.<T>builder()
                .result(result)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

}
