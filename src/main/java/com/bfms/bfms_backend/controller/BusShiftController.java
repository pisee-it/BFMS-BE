package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.BusShiftRequest;
import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.dtos.res.ShiftResponse;
import com.bfms.bfms_backend.entity.BusShift;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.bfms.bfms_backend.service.BusShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
@Tag(name = "Quản lý Ca (Bus Shifts)", description = "Các API quản lý ca chạy của xe và hoàn chuyến")
public class BusShiftController {

    private final BusShiftService busShiftService;

    // 1. POST /api/v1/shifts/node/{nodeId} - Tạo ca chạy mới gắn với nốt chạy
    @PostMapping("/node/{nodeId}")
    @Operation(summary = "Tạo ca chạy mới", description = "Tạo một ca chạy mới gắn với một nốt xe (lượt chạy) cụ thể.")
    public ResponseEntity<BusShift> createBusShift(@PathVariable Integer nodeId, @Valid @RequestBody BusShiftRequest request) {
        return ResponseEntity.ok(busShiftService.createBusShift(nodeId, request));
    }

    // 2. GET /api/v1/shifts/active?routeId={id}
    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách ca đang chạy", description = "Lấy danh sách các ca chạy chưa hoàn thành cho một tuyến xe cụ thể (Realtime).")
    public ResponseEntity<List<BusShiftResponse>> getActiveShifts(@RequestParam Integer routeId) {
        return ResponseEntity.ok(busShiftService.getActiveShiftsByRoute(routeId));
    }

    // 3. POST /api/v1/shifts/{shiftId}/complete - Hoàn thành chuyến xe
    @PostMapping("/{shiftId}/complete")
    @Operation(summary = "Hoàn thành chuyến xe (US-03)", description = "Cập nhật số vé thu được sau khi chuyến xe hoàn thành. Tự động tính toán doanh thu và cập nhật thống kê trong một Transaction.")
    public ResponseEntity<ShiftResponse> completeShift(
            @PathVariable Integer shiftId,
            @Valid @RequestBody CompleteShiftRequest request) {
        return ResponseEntity.ok(busShiftService.completeShift(shiftId, request));
    }
}
