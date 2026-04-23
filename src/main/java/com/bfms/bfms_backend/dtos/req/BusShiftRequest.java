package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.ShiftStatus;

import java.time.LocalTime;

public record BusShiftRequest(
        Integer busId,
        Integer driverId,
        Integer shiftOrder,
        LocalTime plannedDepartureTime,
        LocalTime plannedArrivalTime,
        ShiftStatus status,
        Short direction
) {}
