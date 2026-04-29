package com.bfms.bfms_backend.repository.projection;

import com.bfms.bfms_backend.entity.ShiftStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalTime;

public interface BusShiftProjection {
    @Value("#{target.id}")
    Integer getShiftId();

    @Value("#{target.bus.licensePlate}")
    String getLicensePlate();

    @Value("#{target.driver.fullName}")
    String getDriverName();

    Integer getShiftOrder();
    LocalTime getPlannedDepartureTime();
    ShiftStatus getStatus();

    @Value("#{target.node.direction}")
    Short getDirection();
}
