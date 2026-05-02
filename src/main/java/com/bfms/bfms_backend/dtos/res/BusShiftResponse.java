package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.bfms.bfms_backend.entity.ShiftStatus;

import java.time.LocalTime;

@Schema(description = "Phản hồi thông tin ca chạy")
public record BusShiftResponse(
        @Schema(description = "ID của ca chạy", example = "100")
        Integer shiftId,

        @Schema(description = "Biển số xe thực hiện ca", example = "29B-123.45")
        String licensePlate,

        @Schema(description = "Họ tên tài xế", example = "Nguyễn Văn A")
        String driverName,

        @Schema(description = "Thứ tự ca trong ngày", example = "1")
        Integer shiftOrder,

        @Schema(description = "Giờ xuất bến dự kiến", example = "05:30:00")
        LocalTime plannedDepartureTime,

        @Schema(description = "Trạng thái ca (PENDING, RUNNING, COMPLETED, CANCELLED)")
        ShiftStatus status,

        @Schema(description = "Hướng đi (1: A->B, 2: B->A)", example = "1")
        Short direction
) {}
