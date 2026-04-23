package com.bfms.bfms_backend.dtos.req;

import java.math.BigDecimal;
import java.time.LocalTime;

public record RouteRequest(
        String routeNumber,
        String stopA,
        String stopB,
        String path,
        BigDecimal distanceAB,
        BigDecimal distanceBA,
        LocalTime operationStart,
        LocalTime operationEnd,
        BigDecimal price
) {
}
