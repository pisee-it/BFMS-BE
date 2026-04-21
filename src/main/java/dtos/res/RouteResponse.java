package dtos.res;

import java.math.BigDecimal;
import java.time.LocalTime;

public record RouteResponse(
        Integer id,
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
