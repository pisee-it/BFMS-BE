package com.bfms.bfms_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 1. Hệ thống & Chung
    UNCATEGORIZED_EXCEPTION("SYS_999", "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("SYS_001", "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("AUTH_001", "Tên đăng nhập hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("AUTH_002", "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    INVALID_DATE_RANGE("SYS_002", "Ngày kết thúc phải sau ngày bắt đầu", HttpStatus.BAD_REQUEST),
    INVALID_TIME_RANGE("SYS_003", "Giờ kết thúc phải sau giờ bắt đầu", HttpStatus.BAD_REQUEST),

    // 2. Tuyến xe (Route)
    ROUTE_NOT_FOUND("ROUTE_404", "Không tìm thấy tuyến xe", HttpStatus.NOT_FOUND),
    INVALID_ROUTE_DISTANCE("ROUTE_001", "Khoảng cách tuyến xe không được nhỏ hơn 0", HttpStatus.BAD_REQUEST),
    ROUTE_ALREADY_EXISTS("ROUTE_002", "Số tuyến đã tồn tại trên hệ thống", HttpStatus.BAD_REQUEST),

    // 3. Xe buýt (Bus)
    BUS_NOT_FOUND("BUS_404", "Không tìm thấy xe", HttpStatus.NOT_FOUND),
    BUS_ALREADY_EXISTS("BUS_001", "Biển số xe đã tồn tại trên hệ thống", HttpStatus.BAD_REQUEST),

    // 4. Người dùng (User)
    USER_NOT_FOUND("USER_404", "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("NOTIF_404", "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_NOT_FOUND("AUTH_003", "Refresh token không tồn tại trong hệ thống", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_EXPIRED("AUTH_004", "Refresh token đã hết hạn. Vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),

    // 5. Hợp đồng & Quảng cáo (Ads)
    AD_CONTRACT_NOT_FOUND("AD_404", "Không tìm thấy hợp đồng quảng cáo", HttpStatus.NOT_FOUND),
    AD_COMPANY_NOT_FOUND("AD_001", "Không tìm thấy công ty quảng cáo", HttpStatus.NOT_FOUND),
    AD_COMPANY_ALREADY_EXISTS("AD_002", "Công ty quảng cáo này đã tồn tại (trùng mã số thuế)", HttpStatus.BAD_REQUEST),
    AD_CONTRACT_NOT_APPROVED("AD_003", "Hợp đồng chưa được phê duyệt hoặc thanh toán", HttpStatus.BAD_REQUEST),
    AD_CONTRACT_LIMIT_REACHED("AD_004", "Số lượng xe được gán đã đạt giới hạn của hợp đồng", HttpStatus.BAD_REQUEST),
    BUS_ALREADY_ADVERTISED("AD_005", "Xe này đã có quảng cáo đang hoạt động", HttpStatus.BAD_REQUEST),

    // 6. Nốt & Ca chạy (Node & Shift)
    NODE_NOT_FOUND("NODE_404", "Không tìm thấy nốt xe", HttpStatus.NOT_FOUND),
    SHIFT_NOT_FOUND("SHIFT_404", "Không tìm thấy ca chạy", HttpStatus.NOT_FOUND),
    SHIFT_ALREADY_COMPLETED("SHIFT_001", "Ca chạy đã hoàn thành, không thể thay đổi", HttpStatus.BAD_REQUEST),
    INVALID_SHIFT_DATE("SHIFT_002", "Ngày thực hiện ca chạy không khớp với lịch trình", HttpStatus.BAD_REQUEST),
    SHIFT_TIME_OUT_OF_ROUTE_RANGE("SHIFT_003", "Giờ ca chạy nằm ngoài khung giờ hoạt động của tuyến",
            HttpStatus.BAD_REQUEST),
    NODE_ALREADY_EXISTS("NODE_001", "Nốt xe này đã tồn tại cho tuyến và ngày này", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
