package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.req.RouteRequest;
import com.bfms.bfms_backend.dtos.res.RouteResponse;
import com.bfms.bfms_backend.entity.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    RouteResponse toResponse(Route route);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buses", ignore = true)
    Route toEntity(RouteRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buses", ignore = true)
    void updateEntity(RouteRequest request, @MappingTarget Route route);
}
