package com.josephhieu.vaccinebackend.common.exception;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Bộ xử lý lỗi tập trung cho toàn bộ ứng dụng.
 * Giúp chuẩn hóa dữ liệu trả về khi hệ thống xảy ra lỗi.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý các ngoại lệ được định nghĩa cụ thể trong ứng dụng thông qua AppException.
     */
    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {

        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    /**
     * Xử lý các ngoại lệ chưa được phân loại (Uncategorized Exception).
     * Đảm bảo hệ thống không trả về lỗi thô (stack trace) ra bên ngoài.
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<?>> handleRuntimeException(Exception exception) {
        // Log lỗi tại đây để admin có thể kiểm tra sau này
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();

        return ResponseEntity.internalServerError().body(apiResponse);
    }

    /**
     * Xử lý lỗi khi dữ liệu đầu vào không hợp lệ (Validation Error).
     * Ví dụ: Để trống các ô Username, Password....
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException exception) {
        // 1. Lấy mã lỗi được định nghĩa trong DTO (ví dụ: "MISSING_INFO")
        String enumKey = exception.getBindingResult().getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.MISSING_INFO; // Mặc định là lỗi thiếu thông tin

        try {
            // 2. Ánh xạ chuỗi từ DTO sang Enum ErrorCode để lấy mã 1008 và message chuẩn
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            // Nếu không tìm thấy enum tương ứng, giữ nguyên mặc định
        }

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
