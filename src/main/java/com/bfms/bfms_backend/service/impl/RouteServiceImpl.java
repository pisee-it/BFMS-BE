package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.RouteRequest;
import com.bfms.bfms_backend.dtos.res.RouteResponse;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.mapper.RouteMapper;
import jakarta.transaction.Transactional;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.RouteService;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final com.bfms.bfms_backend.util.EntityLookupHelper lookupHelper;

    public RouteServiceImpl(RouteRepository routeRepository, RouteMapper routeMapper, com.bfms.bfms_backend.util.EntityLookupHelper lookupHelper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.lookupHelper = lookupHelper;
    }


    @Override
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(routeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RouteResponse getRouteById(Integer id) {
        Route route = lookupHelper.getRoute(id);
        return routeMapper.toResponse(route);
    }


    @Override
    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        validateRouteDistances(request.distanceAB(), request.distanceBA());

        Route route = routeMapper.toEntity(request);

        BigDecimal autoPrice = calculateAutomaticPrice(request.distanceAB(), request.distanceBA());
        route.setPrice(autoPrice);

        return routeMapper.toResponse(routeRepository.save(route));
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Integer id, RouteRequest request) {
        Route route = lookupHelper.getRoute(id);

        routeMapper.updateEntity(request, route);
        return routeMapper.toResponse(routeRepository.save(route));
    }


    @Override
    @Transactional
    public void deleteRoute(Integer id) {
        if (!routeRepository.existsById(id)) {
            throw new AppException(ErrorCode.ROUTE_NOT_FOUND);
        }
        routeRepository.deleteById(id);
    }

    private BigDecimal calculateAutomaticPrice(BigDecimal distAB, BigDecimal distBA) {
        BigDecimal average = distAB.add(distBA)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        double avg = average.doubleValue();

        if (avg < 15) return BigDecimal.valueOf(8000);
        if (avg < 25) return BigDecimal.valueOf(10000);
        if (avg < 30) return BigDecimal.valueOf(12000);
        if (avg < 40) return BigDecimal.valueOf(15000);
        return BigDecimal.valueOf(20000);
    }

    private void validateRouteDistances(BigDecimal ab, BigDecimal ba) {
        if (ab.compareTo(BigDecimal.ZERO) < 0 || ba.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.INVALID_ROUTE_DISTANCE);
        }
    }
}
