package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.BusShiftRequest;
import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.dtos.res.ShiftResponse;
import com.bfms.bfms_backend.entity.BusShift;
import jakarta.validation.Valid;
import com.bfms.bfms_backend.service.impl.BusShiftServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class BusShiftController {

    private final BusShiftServiceImpl busShiftService;

    // 1. POST /api/v1/shifts/node/{nodeId} - Tạo ca chạy mới gắn với nốt chạy
    @PostMapping("/node/{nodeId}")
    public ResponseEntity<BusShift> createBusShift(@PathVariable Integer nodeId, @RequestBody BusShiftRequest request) {
        return ResponseEntity.ok(busShiftService.createBusShift(nodeId, request));
    }

    // 2. GET /api/v1/shifts/active?routeId={id}
    @GetMapping("/active")
    public ResponseEntity<List<BusShiftResponse>> getActiveShifts(@RequestParam Integer routeId) {
        return ResponseEntity.ok(busShiftService.getActiveShiftsByRoute(routeId));
    }

    // 3. POST /api/v1/shifts/{shiftId}/complete - Hoàn thành chuyến xe
    @PostMapping("/{shiftId}/complete")
    public ResponseEntity<ShiftResponse> completeShift(
            @PathVariable Integer shiftId,
            @Valid @RequestBody CompleteShiftRequest request) {
        return ResponseEntity.ok(busShiftService.completeShift(shiftId, request));
    }
}
