package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.dtos.res.ShiftResponse;
import com.bfms.bfms_backend.entity.BusShift;
import com.bfms.bfms_backend.repository.projection.BusShiftProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusShiftMapper {
    BusShiftResponse toBusShiftResponse(BusShiftProjection projection);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "node", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "totalSingleTickets", ignore = true)
    @Mapping(target = "totalMonthlyTickets", ignore = true)
    @Mapping(target = "shiftRevenue", ignore = true)
    @Mapping(target = "createdAt", ignore = true)

    com.bfms.bfms_backend.entity.BusShift toEntity(com.bfms.bfms_backend.dtos.req.BusShiftRequest request);

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
