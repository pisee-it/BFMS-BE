package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.CostRequest;
import com.bfms.bfms_backend.dtos.res.CostResponse;
import com.bfms.bfms_backend.service.CostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/costs")
@RequiredArgsConstructor
@Tag(name = "Operational Costs", description = "Quản lý chi phí vận hành")
public class CostController {

    private final CostService costService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Lấy danh sách chi phí", description = "Lọc theo routeId và dải ngày")
    public ResponseEntity<List<CostResponse>> getCosts(
            @RequestParam(required = false) Integer routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(costService.getCosts(routeId, startDate, endDate));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Thêm mới chi phí")
    public ResponseEntity<CostResponse> create(@RequestBody @Valid CostRequest request) {
        return ResponseEntity.ok(costService.createCost(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Cập nhật chi phí")
    public ResponseEntity<CostResponse> update(@PathVariable Integer id, @RequestBody @Valid CostRequest request) {
        return ResponseEntity.ok(costService.updateCost(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Xóa chi phí")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        costService.deleteCost(id);
        return ResponseEntity.noContent().build();
    }
}
