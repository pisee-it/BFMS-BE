package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.res.NodeResponse;
import com.bfms.bfms_backend.entity.Node;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BusShiftMapper.class})
public interface NodeMapper {
    @Mapping(source = "route.routeNumber", target = "routeName")
    NodeResponse toResponse(Node node);
}
