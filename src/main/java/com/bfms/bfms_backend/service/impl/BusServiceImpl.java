package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.BusRequest;
import com.bfms.bfms_backend.dtos.res.BusResponse;
import com.bfms.bfms_backend.entity.Bus;
import com.bfms.bfms_backend.entity.BusStatus;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.mapper.BusMapper;
import com.bfms.bfms_backend.repository.BusRepository;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.BusService;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final BusMapper busMapper;

    public BusServiceImpl(BusRepository busRepository, RouteRepository routeRepository, BusMapper busMapper) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.busMapper = busMapper;
    }

    @Override
    @Transactional
    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BusResponse createBus(BusRequest request) {
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROUTE_NOT_FOUND));

        Bus bus = busMapper.toEntity(request);
        bus.setRoute(route);
        
        return busMapper.toResponse(busRepository.save(bus));
    }

    @Override
    @Transactional
    public BusResponse updateBus(Integer id, BusRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUS_NOT_FOUND));

        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROUTE_NOT_FOUND));

        busMapper.updateEntity(request, bus);
        bus.setRoute(route);
        
        return busMapper.toResponse(busRepository.save(bus));
    }

    @Override
    @Transactional
    public void deleteBus(Integer id) {
        if (!busRepository.existsById(id)) {
            throw new AppException(ErrorCode.BUS_NOT_FOUND);
        }
        busRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void sellBus(Integer id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUS_NOT_FOUND));

        bus.setStatus(BusStatus.SOLD);
        busRepository.save(bus);
    }
}
