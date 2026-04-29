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
    private final BusMapper busMapper;
    private final com.bfms.bfms_backend.util.EntityLookupHelper lookupHelper;

    public BusServiceImpl(BusRepository busRepository, BusMapper busMapper, com.bfms.bfms_backend.util.EntityLookupHelper lookupHelper) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
        this.lookupHelper = lookupHelper;
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
        Route route = lookupHelper.getRoute(request.routeId());

        Bus bus = busMapper.toEntity(request);
        bus.setRoute(route);
        
        return busMapper.toResponse(busRepository.save(bus));
    }


    @Override
    @Transactional
    public BusResponse updateBus(Integer id, BusRequest request) {
        Bus bus = lookupHelper.getBus(id);
        Route route = lookupHelper.getRoute(request.routeId());

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
        Bus bus = lookupHelper.getBus(id);

        bus.setStatus(BusStatus.SOLD);
        busRepository.save(bus);
    }

}
