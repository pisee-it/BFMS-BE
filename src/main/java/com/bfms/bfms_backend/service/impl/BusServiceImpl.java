package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.BusRequest;
import com.bfms.bfms_backend.dtos.res.BusResponse;
import com.bfms.bfms_backend.entity.Bus;
import com.bfms.bfms_backend.entity.BusStatus;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.mapper.BusMapper;
import com.bfms.bfms_backend.repository.BusRepository;
import com.bfms.bfms_backend.service.AuditService;
import com.bfms.bfms_backend.service.BusService;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.util.EntityLookupHelper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {
    private final BusRepository busRepository;
    private final BusMapper busMapper;
    private final EntityLookupHelper lookupHelper;
    private final AuditService auditService;

    public BusServiceImpl(BusRepository busRepository, BusMapper busMapper, EntityLookupHelper lookupHelper, AuditService auditService) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
        this.lookupHelper = lookupHelper;
        this.auditService = auditService;
    }


    @Override
    @Transactional
    public List<BusResponse> getAllBuses() {
        return busRepository.findBy().stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BusResponse createBus(BusRequest request) {
        if (busRepository.findByLicensePlate(request.licensePlate()).isPresent()) {
            throw new AppException(ErrorCode.BUS_ALREADY_EXISTS);
        }

        Route route = lookupHelper.getRoute(request.routeId());

        Bus bus = busMapper.toEntity(request);
        bus.setRoute(route);
        Bus savedBus = busRepository.save(bus);
        
        auditService.log("CREATE_BUS", "Tạo mới xe buýt: " + savedBus.getLicensePlate());
        return busMapper.toResponse(savedBus);
    }


    @Override
    @Transactional
    public BusResponse updateBus(Integer id, BusRequest request) {
        Bus bus = lookupHelper.getBus(id);

        if (!bus.getLicensePlate().equals(request.licensePlate())) {
            if (busRepository.findByLicensePlate(request.licensePlate()).isPresent()) {
                throw new AppException(ErrorCode.BUS_ALREADY_EXISTS);
            }
        }

        Route route = lookupHelper.getRoute(request.routeId());

        busMapper.updateEntity(request, bus);
        bus.setRoute(route);
        Bus updatedBus = busRepository.save(bus);
        
        auditService.log("UPDATE_BUS", "Cập nhật xe buýt ID: " + id + ", biển số: " + updatedBus.getLicensePlate());
        return busMapper.toResponse(updatedBus);
    }


    @Override
    @Transactional
    public void deleteBus(Integer id) {
        Bus bus = lookupHelper.getBus(id);
        busRepository.deleteById(id);
        auditService.log("DELETE_BUS", "Xóa xe buýt ID: " + id + ", biển số: " + bus.getLicensePlate());
    }

    @Override
    @Transactional
    public void sellBus(Integer id) {
        Bus bus = lookupHelper.getBus(id);

        bus.setStatus(BusStatus.SOLD);
        busRepository.save(bus);
        auditService.log("SELL_BUS", "Bán xe buýt ID: " + id + ", biển số: " + bus.getLicensePlate());
    }

}
