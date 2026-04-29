package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.RouteRequest;
import com.bfms.bfms_backend.dtos.res.RouteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.bfms.bfms_backend.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Quản lý Tuyến (Routes)", description = "Các API quản lý thông tin tuyến xe buýt")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // 1. Xem danh sách (Dành cho ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách tuyến", description = "Trả về danh sách các tuyến xe buýt đang hoạt động. Quyền: ADMIN")
    public ResponseEntity<List<RouteResponse>> getAll() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // 2. Tạo mới tuyến (Dành cho ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo tuyến mới", description = "Thêm một tuyến xe buýt mới. Giá vé sẽ được tự động tính theo khoảng cách. Quyền: ADMIN")
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.createRoute(request));
    }

    // 3. Cập nhật (Dành cho ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật tuyến", description = "Cập nhật thông tin chi tiết của một tuyến xe buýt. Quyền: ADMIN")
    public ResponseEntity<RouteResponse> update(@PathVariable Integer id, @Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.updateRoute(id, request));
    }

    // 4. Xóa (Dành cho ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa tuyến", description = "Loại bỏ một tuyến xe buýt khỏi hệ thống. Quyền: ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
