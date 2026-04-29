package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.repository.projection.RouteProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Optional<Route> findByRouteNumber(String routeNumber);

    // Lấy toàn bộ tuyến đường dưới dạng Projection
    List<RouteProjection> findBy();
}
