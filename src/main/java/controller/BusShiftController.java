package controller;

import dtos.req.BusShiftRequest;
import dtos.req.CompleteShiftRequest;
import dtos.res.BusShiftResponse;
import dtos.res.ShiftResponse;
import entity.BusShift;
import jakarta.validation.Valid;
import service.BusShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BusShiftController {

    private final BusShiftService busShiftService;

    // 1. POST /nodes/{nodeId}/shifts - Tạo ca chạy mới gắn với nốt chạy
    @PostMapping("/nodes/{nodeId}/shifts")
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