package dtos.res;

import entity.BusStatus;

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
