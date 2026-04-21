package dtos.res;

import entity.ShiftStatus;

import java.math.BigDecimal;
import java.time.LocalTime;

public record ShiftResponse(
        Integer id,
        String busLicensePlate,
        String driverName,
        Integer shiftOrder,
        ShiftStatus status,
        BigDecimal shiftRevenue,
        LocalTime plannedDepartureTime
) {}