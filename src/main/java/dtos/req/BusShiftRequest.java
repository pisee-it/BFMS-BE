package dtos.req;

import entity.ShiftStatus;

import java.time.LocalTime;

public record BusShiftRequest(
        Integer busId,
        Long driverId,
        Integer shiftOrder,
        LocalTime plannedDepartureTime,
        LocalTime plannedArrivalTime,
        ShiftStatus status
) {}
