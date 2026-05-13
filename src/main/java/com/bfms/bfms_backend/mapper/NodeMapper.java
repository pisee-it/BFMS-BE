package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.dtos.res.NodeResponse;
import com.bfms.bfms_backend.entity.Node;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {BusShiftMapper.class})
public interface NodeMapper {
    @Mapping(source = "route.routeNumber", target = "routeName")
    NodeResponse toResponse(Node node);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "route", ignore = true)
    @Mapping(target = "totalPassengers", ignore = true)
    @Mapping(target = "shifts", ignore = true)
    void updateEntity(@MappingTarget Node node, NodeRequest request);
}
