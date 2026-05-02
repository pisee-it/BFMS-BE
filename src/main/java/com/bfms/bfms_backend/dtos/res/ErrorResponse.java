package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Cấu trúc phản hồi lỗi chuẩn hóa của hệ thống")
public record ErrorResponse(
        @Schema(description = "Mã trạng thái HTTP", example = "400")
        int status,

        @Schema(description = "Mã lỗi nội bộ (ErrorCode)", example = "INVALID_INPUT")
        String code,

        @Schema(description = "Thông báo lỗi chi tiết (Tiếng Việt)", example = "Dữ liệu đầu vào không hợp lệ")
        String message,

        @Schema(description = "Thời điểm xảy ra lỗi")
        LocalDateTime timestamp,

        @Schema(description = "Danh sách chi tiết lỗi validation (nếu có)")
        Map<String, String> errors) {
    
    public ErrorResponse(int status, String code, String message) {
        this(status, code, message, LocalDateTime.now(), null);
    }

    public ErrorResponse(int status, String code, String message, Map<String, String> errors) {
        this(status, code, message, LocalDateTime.now(), errors);
    }
}
