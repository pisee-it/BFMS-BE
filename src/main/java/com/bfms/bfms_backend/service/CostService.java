package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.CostRequest;
import com.bfms.bfms_backend.dtos.res.CostResponse;

import java.time.LocalDate;
import java.util.List;

public interface CostService {
    List<CostResponse> getCosts(Integer routeId, LocalDate startDate, LocalDate endDate);

    CostResponse createCost(CostRequest request);

    CostResponse updateCost(Integer id, CostRequest request);

    void deleteCost(Integer id);
}
