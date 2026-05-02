package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Phản hồi thông tin thông báo")
public record NotificationResponse(
    @Schema(description = "ID của thông báo", example = "1")
    Integer id,

    @Schema(description = "Nội dung thông báo", example = "Hợp đồng quảng cáo Alpha đã được phê duyệt.")
    String message,

    @Schema(description = "Trạng thái đã đọc hay chưa", example = "false")
    Boolean isRead,

    @Schema(description = "Thời điểm gửi thông báo")
    LocalDateTime createdAt
) {}
