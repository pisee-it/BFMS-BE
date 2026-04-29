package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.BusRequest;
import com.bfms.bfms_backend.dtos.res.BusResponse;

import java.util.List;

public interface BusService {
    List<BusResponse> getAllBuses();

    BusResponse createBus(BusRequest request);

    BusResponse updateBus(Integer id, BusRequest request);

    void deleteBus(Integer id);

    void sellBus(Integer id);
}
