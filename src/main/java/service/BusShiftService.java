package service;

import dtos.req.BusShiftRequest;
import dtos.req.CompleteShiftRequest;
import dtos.res.BusShiftResponse;
import dtos.res.ShiftResponse;
import entity.BusShift;

import java.util.List;

public interface BusShiftService {
    // 1. Tạo mới một Ca chạy cho Nốt chạy
    BusShift createBusShift(Integer nodeId, BusShiftRequest request);

    // 2. Lấy danh sách ca chạy đang hoạt động theo Tuyến
    List<BusShiftResponse> getActiveShiftsByRoute(Integer routeId);

    // 1. Xử lý hoàn thành ca chạy và tính toán doanh thu
    ShiftResponse completeShift(Integer shiftId, CompleteShiftRequest request);
}
