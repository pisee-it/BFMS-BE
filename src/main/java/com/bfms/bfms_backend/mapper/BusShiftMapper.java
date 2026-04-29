package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.dtos.res.ShiftResponse;
import com.bfms.bfms_backend.entity.BusShift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusShiftMapper {

    @Mapping(source = "id", target = "shiftId")
    @Mapping(source = "bus.licensePlate", target = "licensePlate")
    @Mapping(source = "driver.fullName", target = "driverName")
    BusShiftResponse toBusShiftResponse(BusShift shift);

    @Mapping(source = "bus.licensePlate", target = "busLicensePlate")
    @Mapping(source = "driver.fullName", target = "driverName")
    @Mapping(source = "totalSingleTickets", target = "singleTicketCount")
    @Mapping(source = "totalMonthlyTickets", target = "monthlyTicketCount")
    ShiftResponse toShiftResponse(BusShift shift);
}
