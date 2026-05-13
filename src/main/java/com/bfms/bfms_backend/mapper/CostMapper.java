package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.req.CostRequest;
import com.bfms.bfms_backend.dtos.res.CostResponse;
import com.bfms.bfms_backend.entity.OperationalCost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OperationalCost toEntity(CostRequest request);

    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "routeNumber", source = "route.routeNumber")
    CostResponse toResponse(OperationalCost entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget OperationalCost entity, CostRequest request);
}
