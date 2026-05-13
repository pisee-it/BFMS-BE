package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.CostRequest;
import com.bfms.bfms_backend.dtos.res.CostResponse;
import com.bfms.bfms_backend.entity.OperationalCost;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.mapper.CostMapper;
import com.bfms.bfms_backend.repository.OperationalCostRepository;
import com.bfms.bfms_backend.service.AuditService;
import com.bfms.bfms_backend.service.CostService;
import com.bfms.bfms_backend.util.EntityLookupHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostServiceImpl implements CostService {

    private final OperationalCostRepository costRepository;
    private final CostMapper costMapper;
    private final EntityLookupHelper lookupHelper;
    private final AuditService auditService;

    @Override
    public List<CostResponse> getCosts(Integer routeId, LocalDate startDate, LocalDate endDate) {
        List<OperationalCost> costs;
        if (routeId != null) {
            costs = costRepository.findAllByRouteIdAndCostDateBetween(routeId, startDate, endDate);
        } else {
            costs = costRepository.findAllByCostDateBetween(startDate, endDate);
        }
        return costs.stream()
                .map(costMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CostResponse createCost(CostRequest request) {
        Route route = lookupHelper.getRoute(request.routeId());
        OperationalCost cost = costMapper.toEntity(request);
        cost.setRoute(route);

        OperationalCost saved = costRepository.save(cost);
        auditService.log("CREATE_COST", "Created cost for route " + route.getRouteNumber() + ": " + saved.getAmount());
        
        return costMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CostResponse updateCost(Integer id, CostRequest request) {
        OperationalCost cost = costRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COST_NOT_FOUND));
        
        Route route = lookupHelper.getRoute(request.routeId());
        costMapper.updateEntity(cost, request);
        cost.setRoute(route);

        OperationalCost saved = costRepository.save(cost);
        auditService.log("UPDATE_COST", "Updated cost ID " + id);
        
        return costMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCost(Integer id) {
        if (!costRepository.existsById(id)) {
            throw new AppException(ErrorCode.COST_NOT_FOUND);
        }
        costRepository.deleteById(id);
        auditService.log("DELETE_COST", "Deleted cost ID " + id);
    }
}
