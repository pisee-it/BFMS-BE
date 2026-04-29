package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.BusRequest;
import com.bfms.bfms_backend.dtos.res.BusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.bfms.bfms_backend.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buses")
@Tag(name = "Quản lý Xe (Buses)", description = "Các API quản lý danh sách và thông tin xe buýt")
public class BusController {
    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    // 1. Phân quyền ADMIN cho các thao tác quản trị xe
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách xe", description = "Trả về toàn bộ danh sách xe buýt trong hệ thống. Quyền: ADMIN")
    public ResponseEntity<List<BusResponse>> getAll() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo xe mới", description = "Thêm một xe buýt mới vào hệ thống. Quyền: ADMIN")
    public ResponseEntity<BusResponse> create(@Valid @RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.createBus(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin xe", description = "Cập nhật các thuộc tính của xe buýt theo ID. Quyền: ADMIN")
    public ResponseEntity<BusResponse> update(@PathVariable Integer id, @Valid @RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.updateBus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa xe", description = "Xóa bỏ một xe buýt khỏi hệ thống. Quyền: ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}
