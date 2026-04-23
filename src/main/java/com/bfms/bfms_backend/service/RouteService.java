package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.RouteRequest;
import com.bfms.bfms_backend.dtos.res.RouteResponse;

import java.util.List;

public interface RouteService {
    List<RouteResponse> getAllRoutes();
    RouteResponse getRouteById(Integer id);
    RouteResponse createRoute(RouteRequest request);
    RouteResponse updateRoute(Integer id, RouteRequest request);
    void deleteRoute(Integer id);
}
