package service;

import dtos.req.BusRequest;
import dtos.res.BusResponse;

import java.util.List;

public interface BusService {
    List<BusResponse> getAllBuses();
    BusResponse createBus(BusRequest request);
    BusResponse updateBus(Integer id, BusRequest request);
    void deleteBus(Integer id);
    void sellBus(Integer id);
}
