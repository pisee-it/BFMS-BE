package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Optional<Route> findByRouteNumber(String routeNumber);
}
