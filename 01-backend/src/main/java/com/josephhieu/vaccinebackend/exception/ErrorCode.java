package com.josephhieu.vaccinebackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum quản lý danh sách các mã lỗi hệ thống.
 * Mỗi lỗi bao gồm một mã code định danh, thông báo và trạng thái HTTP tương ứng. [cite: 30, 40]
 */
@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa phân loại", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "Tên đăng nhập đã tồn tại!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(1006, "Quyền hạn không tồn tại!", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1009, "Sai username / password", HttpStatus.UNAUTHORIZED),
    USER_LOCKED(1007, "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.", HttpStatus.FORBIDDEN),
    MISSING_INFO(1008, "Vui lòng nhập đầy đủ thông tin", HttpStatus.BAD_REQUEST);
    ;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final  int code;
    private final  String message;
    private final HttpStatus statusCode;
}
