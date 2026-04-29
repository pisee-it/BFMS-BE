package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.req.BusRequest;
import com.bfms.bfms_backend.dtos.res.BusResponse;
import com.bfms.bfms_backend.entity.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(source = "route.id", target = "routeId")
    @Mapping(source = "route.routeNumber", target = "routeNumber")
    BusResponse toResponse(Bus bus);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", ignore = true)
    Bus toEntity(BusRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", ignore = true)
    void updateEntity(BusRequest request, @MappingTarget Bus bus);
}
