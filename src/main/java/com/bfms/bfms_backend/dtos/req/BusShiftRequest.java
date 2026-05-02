package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.ShiftStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Schema(description = "Yêu cầu tạo hoặc cập nhật ca chạy (Bus Shift)")
public record BusShiftRequest(
        @Schema(description = "ID của xe thực hiện ca chạy", example = "1")
        @NotNull(message = "ID xe không được để trống")
        Integer busId,

        @Schema(description = "ID của tài xế thực hiện ca (nhân viên có role STAFF)", example = "2")
        @NotNull(message = "ID tài xế không được để trống")
        Integer driverId,

        @Schema(description = "Thứ tự ca chạy trong ngày của xe", example = "1")
        @NotNull(message = "Thứ tự ca không được để trống")
        @Min(value = 1, message = "Thứ tự ca phải lớn hơn hoặc bằng 1")
        Integer shiftOrder,

        @Schema(description = "Giờ xuất bến dự kiến", example = "05:30:00")
        @NotNull(message = "Giờ đi dự kiến không được để trống")
        LocalTime plannedDepartureTime,

        @Schema(description = "Giờ đến bến dự kiến", example = "06:45:00")
        @NotNull(message = "Giờ đến dự kiến không được để trống")
        LocalTime plannedArrivalTime,

        @Schema(description = "Trạng thái hiện tại của ca chạy")
        @NotNull(message = "Trạng thái ca không được để trống")
        ShiftStatus status,

        @Schema(description = "Hướng đi (1: A->B, 2: B->A)", example = "1")
        @NotNull(message = "Hướng đi không được để trống")
        Short direction
) {}
