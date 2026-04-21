package controller;

import dtos.req.BusRequest;
import dtos.res.BusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.impl.BusServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buses")
public class BusController {
    private final BusServiceImpl busService;

    public BusController(BusServiceImpl busService) {
        this.busService = busService;
    }

    // 1. Phân quyền ADMIN cho các thao tác quản trị xe
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BusResponse>> getAll() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusResponse> create(@RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.createBus(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusResponse> update(@PathVariable Integer id, @RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.updateBus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}
