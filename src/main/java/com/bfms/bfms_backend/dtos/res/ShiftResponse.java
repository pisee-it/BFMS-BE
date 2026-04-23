package com.bfms.bfms_backend.dtos.res;

import com.bfms.bfms_backend.entity.ShiftStatus;

import java.math.BigDecimal;
import java.time.LocalTime;

public record ShiftResponse(
        Integer id,
        String busLicensePlate,
        String driverName,
        Integer shiftOrder,
        ShiftStatus status,
        BigDecimal shiftRevenue,
        LocalTime plannedDepartureTime,
        Short direction
) {}
