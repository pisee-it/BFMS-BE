package com.bfms.bfms_backend.repository.projection;

import com.bfms.bfms_backend.entity.BusStatus;
import org.springframework.beans.factory.annotation.Value;

public interface BusProjection {
    Integer getId();
    String getBusModel();
    String getManufacturer();
    Integer getCapacity();
    Integer getYom();
    String getLicensePlate();
    BusStatus getStatus();
    Boolean getIsAdvertised();

    @Value("#{target.route.id}")
    Integer getRouteId();

    @Value("#{target.route.routeNumber}")
    String getRouteNumber();
}
