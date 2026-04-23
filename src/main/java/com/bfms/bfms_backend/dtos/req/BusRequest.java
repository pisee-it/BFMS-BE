package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.BusStatus;

public record BusRequest(
        Integer routeId,
        String busModel,
        String manufacturer,
        Integer capacity,
        Integer yom,
        String licensePlate,
        BusStatus status,
        Boolean isAdvertised
) {
}
