package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.ShiftStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record BusShiftRequest(
        @NotNull(message = "ID xe không được để trống")
        Integer busId,

        @NotNull(message = "ID tài xế không được để trống")
        Integer driverId,

        @NotNull(message = "Thứ tự ca không được để trống")
        @Min(value = 1, message = "Thứ tự ca phải lớn hơn hoặc bằng 1")
        Integer shiftOrder,

        @NotNull(message = "Giờ đi dự kiến không được để trống")
        LocalTime plannedDepartureTime,

        @NotNull(message = "Giờ đến dự kiến không được để trống")
        LocalTime plannedArrivalTime,

        @NotNull(message = "Trạng thái ca không được để trống")
        ShiftStatus status,

        @NotNull(message = "Hướng đi không được để trống")
        Short direction
) {}
