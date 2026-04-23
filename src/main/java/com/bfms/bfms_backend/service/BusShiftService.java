package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.BusShiftRequest;
import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.dtos.res.ShiftResponse;
import com.bfms.bfms_backend.entity.BusShift;

import java.util.List;

public interface BusShiftService {
    // 1. Tạo mới một Ca chạy cho Nốt chạy
    BusShift createBusShift(Integer nodeId, BusShiftRequest request);

    // 2. Lấy danh sách ca chạy đang hoạt động theo Tuyến
    List<BusShiftResponse> getActiveShiftsByRoute(Integer routeId);

    // 1. Xử lý hoàn thành ca chạy và tính toán doanh thu
    ShiftResponse completeShift(Integer shiftId, CompleteShiftRequest request);
}
