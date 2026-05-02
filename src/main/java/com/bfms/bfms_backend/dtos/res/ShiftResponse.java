package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.bfms.bfms_backend.entity.ShiftStatus;

import java.math.BigDecimal;
import java.time.LocalTime;

@Schema(description = "Phản hồi chi tiết ca chạy sau khi hoàn thành")
public record ShiftResponse(
        @Schema(description = "ID của ca chạy", example = "100")
        Integer id,

        @Schema(description = "Biển số xe", example = "29B-123.45")
        String busLicensePlate,

        @Schema(description = "Họ tên tài xế", example = "Nguyễn Văn A")
        String driverName,

        @Schema(description = "Thứ tự ca trong ngày", example = "1")
        Integer shiftOrder,

        @Schema(description = "Trạng thái ca (COMPLETED)")
        ShiftStatus status,

        @Schema(description = "Doanh thu tính toán được của ca này", example = "450000")
        BigDecimal shiftRevenue,

        @Schema(description = "Giờ xuất bến dự kiến", example = "05:30:00")
        LocalTime plannedDepartureTime,

        @Schema(description = "Hướng đi (1: A->B, 2: B->A)", example = "1")
        Short direction,

        @Schema(description = "Số lượng vé lượt thu được", example = "45")
        Integer singleTicketCount,

        @Schema(description = "Số lượng khách vé tháng ghi nhận", example = "15")
        Integer monthlyTicketCount
) {}
