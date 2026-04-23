package com.bfms.bfms_backend.dtos.res;

import com.bfms.bfms_backend.entity.BusStatus;

public record BusResponse(
        Integer id,
        Integer routeId,
        String routeNumber,
        String busModel,
        String manufacturer,
        Integer capacity,
        Integer yom,
        String licensePlate,
        BusStatus status,
        Boolean isAdvertised
) {
}
