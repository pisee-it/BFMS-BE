package com.bfms.bfms_backend.dtos.res;

import com.bfms.bfms_backend.entity.ShiftStatus;

import java.time.LocalTime;

public record BusShiftResponse(
        Integer shiftId,
        String licensePlate, // Biển số xe
        String driverName,   // Tên tài xế
        Integer shiftOrder,
        LocalTime plannedDepartureTime,
        ShiftStatus status,
        Short direction
) {}
