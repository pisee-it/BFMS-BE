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
import com.bfms.bfms_backend.service.AuditService;
import org.springframework.stereotype.Service;
import com.bfms.bfms_backend.util.EntityLookupHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final EntityLookupHelper lookupHelper;
    private final AuditService auditService;

    public RouteServiceImpl(RouteRepository routeRepository, RouteMapper routeMapper, 
            EntityLookupHelper lookupHelper, AuditService auditService) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
        this.lookupHelper = lookupHelper;
        this.auditService = auditService;
    }


    @Override
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findBy().stream()
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
        if (routeRepository.findByRouteNumber(request.routeNumber()).isPresent()) {
            throw new AppException(ErrorCode.ROUTE_ALREADY_EXISTS);
        }

        if (request.operationEnd().isBefore(request.operationStart())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        validateRouteDistances(request.distanceAB(), request.distanceBA());

        Route route = routeMapper.toEntity(request);

        BigDecimal autoPrice = calculateAutomaticPrice(request.distanceAB(), request.distanceBA());
        route.setPrice(autoPrice);

        Route savedRoute = routeRepository.save(route);
        auditService.log("CREATE_ROUTE", "Tạo mới tuyến xe số: " + savedRoute.getRouteNumber());

        return routeMapper.toResponse(savedRoute);
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Integer id, RouteRequest request) {
        Route route = lookupHelper.getRoute(id);

        if (!route.getRouteNumber().equals(request.routeNumber())) {
            if (routeRepository.findByRouteNumber(request.routeNumber()).isPresent()) {
                throw new AppException(ErrorCode.ROUTE_ALREADY_EXISTS);
            }
        }

        if (request.operationEnd().isBefore(request.operationStart())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        routeMapper.updateEntity(request, route);
        Route updatedRoute = routeRepository.save(route);
        
        auditService.log("UPDATE_ROUTE", "Cập nhật tuyến xe ID: " + id + ", số: " + updatedRoute.getRouteNumber());
        
        return routeMapper.toResponse(updatedRoute);
    }


    @Override
    @Transactional
    public void deleteRoute(Integer id) {
        Route route = lookupHelper.getRoute(id);
        routeRepository.delete(route);
        auditService.log("DELETE_ROUTE", "Xóa tuyến xe ID: " + id + ", số: " + route.getRouteNumber());
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
