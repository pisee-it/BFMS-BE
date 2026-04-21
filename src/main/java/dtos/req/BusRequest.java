package dtos.req;

import entity.BusStatus;

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
