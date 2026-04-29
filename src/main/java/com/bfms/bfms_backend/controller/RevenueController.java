package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.res.RevenueResponse;
import com.bfms.bfms_backend.service.EconomyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/revenue")
@RequiredArgsConstructor
@Tag(name = "Doanh thu (Revenue)", description = "Các API thống kê doanh thu tổng hợp của hệ thống")
public class RevenueController {

    private final EconomyReportService economyReportService;

    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Xem tổng doanh thu (US-01)", description = "Tính toán và trả về tổng doanh thu vé + quảng cáo, chi phí, thuế và lợi nhuận ròng. Quyền: OWNER, ADMIN")
    public ResponseEntity<RevenueResponse> getSystemTotalRevenue(
            @RequestParam(defaultValue = "day") String timeframe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        RevenueResponse response = economyReportService.getSystemTotalRevenue(timeframe, date);
        return ResponseEntity.ok(response);
    }
}
