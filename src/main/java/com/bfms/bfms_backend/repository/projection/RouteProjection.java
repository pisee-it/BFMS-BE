package com.bfms.bfms_backend.repository.projection;

import java.math.BigDecimal;
import java.time.LocalTime;

public interface RouteProjection {
    Integer getId();
    String getRouteNumber();
    String getStopA();
    String getStopB();
    String getPath();
    BigDecimal getDistanceAB();
    BigDecimal getDistanceBA();
    LocalTime getOperationStart();
    LocalTime getOperationEnd();
    BigDecimal getPrice();
}
