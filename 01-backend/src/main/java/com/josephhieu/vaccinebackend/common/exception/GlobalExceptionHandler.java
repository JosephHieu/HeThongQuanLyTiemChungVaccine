package com.josephhieu.vaccinebackend.common.exception;

import com.josephhieu.vaccinebackend.common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
        log.warn("Truy cập bị từ chối: {}", exception.getMessage());
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getBindingResult().getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_INFO;

        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            log.error("Không tìm thấy mã lỗi validation: {}", enumKey);
        }

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        log.error("Phương thức không được hỗ trợ: {}", exception.getMethod());
        return ResponseEntity.status(405).body(
                ApiResponse.builder()
                        .code(405)
                        .message("Phương thức HTTP không được hỗ trợ cho đường dẫn này")
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        log.error("Lỗi định dạng JSON gửi lên: {}", exception.getMessage());
        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .code(400)
                        .message("Dữ liệu gửi lên không đúng định dạng JSON")
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<?>> handleRuntimeException(Exception exception) {
        log.error("LỖI HỆ THỐNG CHƯA PHÂN LOẠI: ", exception); // Log đầy đủ stack trace vào file log

        return ResponseEntity.internalServerError().body(
                ApiResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build()
        );
    }
}
