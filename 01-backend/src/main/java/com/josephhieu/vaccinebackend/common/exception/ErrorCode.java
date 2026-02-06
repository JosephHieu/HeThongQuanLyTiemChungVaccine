package com.josephhieu.vaccinebackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum quản lý danh sách các mã lỗi hệ thống.
 * Mỗi lỗi bao gồm một mã code định danh, thông báo và trạng thái HTTP tương ứng. [cite: 30, 40]
 */
@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa phân loại", HttpStatus.INTERNAL_SERVER_ERROR),

    // SECURITY & AUTH (1001 - 1099)
    USER_EXISTED(1001, "Tên đăng nhập đã tồn tại!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(1006, "Quyền hạn không tồn tại!", HttpStatus.BAD_REQUEST),
    USER_LOCKED(1007, "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.", HttpStatus.FORBIDDEN),
    MISSING_INFO(1008, "Vui lòng nhập đầy đủ thông tin", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1009, "Sai username / password", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1010, "Bạn không có quyền truy cập chức năng này", HttpStatus.FORBIDDEN),
    INVALID_INFO(1011, "Thông tin yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    // INVENTORY ERRORS (1100 - 1199)
    INVENTORY_NOT_FOUND(1101, "Lô vắc-xin không tồn tại trong hệ thống", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(1102, "Số lượng tồn kho không đủ để thực hiện xuất", HttpStatus.BAD_REQUEST),
    VACCINE_TYPE_NOT_FOUND(1103, "Loại vắc-xin không hợp lệ", HttpStatus.BAD_REQUEST),
    SUPPLIER_NOT_FOUND(1104, "Nhà cung cấp không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_IMPORT_QUANTITY(1105, "Số lượng nhập kho phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    BATCH_ALREADY_EXISTS(1106, "Số lô này đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    INVALID_EXPIRY_DATE(1107, "Hạn sử dụng không hợp lệ (phải sau ngày hiện tại)",  HttpStatus.BAD_REQUEST),
    BATCH_NOT_FOUND(1108, "Không tìm thấy thông tin lô vắc-xin yêu cầu", HttpStatus.NOT_FOUND),

    // MEDICAL ERRORS (1200 - 1299)
    PATIENT_NOT_FOUND(1201, "Không tìm thấy thông tin bệnh nhân trong hệ thống", HttpStatus.NOT_FOUND),
    PRESCRIPTION_INVALID(1202, "Thông tin kê đơn không hợp lệ", HttpStatus.BAD_REQUEST),
    HISTORY_NOT_FOUND(1203, "Không tìm thấy lịch sử tiêm chủng của bệnh nhân", HttpStatus.NOT_FOUND),
    EPIDEMIC_NOT_FOUND(1204, "Không tìm thấy thông tin dịch bệnh yêu cầu", HttpStatus.NOT_FOUND),

    // VACCINATION ERRORS (1300-1399)
    VACCINE_OUT_OF_STOCK(1301, "Vắc-xin này hiện đã hết hàng trong kho", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_FOUND(1302, "Lịch tiêm chủng không tồn tại hoặc đã bị hủy", HttpStatus.NOT_FOUND),
    REGISTRATION_FAILED(1303, "Đăng ký tiêm chủng thất bại, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_REGISTERED(1304, "Bạn đã đăng ký mũi tiêm này rồi", HttpStatus.BAD_REQUEST),
    SCHEDULE_FULL(1305, "Đợt tiêm chủng này đã đủ số lượng người đăng ký", HttpStatus.BAD_REQUEST),
    SCHEDULE_EXPIRED(1306, "Lịch tiêm chủng này đã kết thúc, không thể đăng ký", HttpStatus.BAD_REQUEST),
    REGISTRATION_NOT_FOUND(1307, "Không tìm thấy thông tin đăng ký tiêm chủng", HttpStatus.NOT_FOUND),
    CANNOT_CANCEL(1308, "Không thể hủy đăng ký đã hoàn thành hoặc quá hạn", HttpStatus.BAD_REQUEST),
    REGISTRATION_EXPIRED(1309, "Đăng ký này đã quá thời gian xử lý", HttpStatus.BAD_REQUEST),
    INVALID_REGISTRATION_STATUS(1310, "Trạng thái đăng ký không hợp lệ để thực hiện thao tác này", HttpStatus.BAD_REQUEST),
    VACCINATION_ALREADY_COMPLETED(1311, "Mũi tiêm này đã được xác nhận hoàn thành trước đó", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(1312, "Số lượng vắc-xin trong lô đã hết, không thể thực hiện tiêm", HttpStatus.BAD_REQUEST),
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
