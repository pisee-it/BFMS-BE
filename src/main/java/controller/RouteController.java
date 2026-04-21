package controller;

import dtos.req.RouteRequest;
import dtos.res.RouteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.impl.RouteServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {
    private final RouteServiceImpl routeService;

    public RouteController(RouteServiceImpl routeService) {
        this.routeService = routeService;
    }

    // 1. Xem danh sách (Dành cho ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RouteResponse>> getAll() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // 2. Tạo mới tuyến (Dành cho ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteResponse> create(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.createRoute(request));
    }

    // 3. Cập nhật (Dành cho ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteResponse> update(@PathVariable Integer id, @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.updateRoute(id, request));
    }

    // 4. Xóa (Dành cho ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
