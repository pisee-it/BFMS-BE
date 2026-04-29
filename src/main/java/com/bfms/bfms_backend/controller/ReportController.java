package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.res.RouteReportResponse;
import com.bfms.bfms_backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Báo cáo (Reports)", description = "Các API xuất báo cáo tài chính chi tiết theo tuyến (Excel/JSON)")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/export")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Xuất báo cáo doanh thu tuyến (US-11)", description = "Lấy báo cáo chi tiết cho một tuyến xe trong khoảng thời gian. Hỗ trợ định dạng JSON hoặc EXCEL. Quyền: OWNER")
    public ResponseEntity<?> exportReport(
            @RequestParam Integer routeId,
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // Mặc định là tháng hiện tại nếu không truyền ngày
        if (startDate == null) {
            startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        if ("excel".equalsIgnoreCase(format)) {
            byte[] excelContent = reportService.exportRouteReportToExcel(routeId, startDate, endDate);
            
            String fileName = "Bao-cao-tuyen-" + routeId + "-" + LocalDate.now() + ".xlsx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelContent);
        }

        // Mặc định trả về JSON
        RouteReportResponse response = reportService.getRouteReport(routeId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
