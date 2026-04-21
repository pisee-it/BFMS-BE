package service;

import dtos.req.RouteRequest;
import dtos.res.RouteResponse;

import java.util.List;

public interface RouteService {
    List<RouteResponse> getAllRoutes();
    RouteResponse getRouteById(Integer id);
    RouteResponse createRoute(RouteRequest request);
    RouteResponse updateRoute(Integer id, RouteRequest request);
    void deleteRoute(Integer id);
}
