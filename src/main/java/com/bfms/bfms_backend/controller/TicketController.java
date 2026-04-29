package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.res.TicketStatisticsResponse;
import com.bfms.bfms_backend.service.TicketService;
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
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Vé xe (Tickets)", description = "Các API thống kê số lượng vé lượt và vé tháng")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Thống kê vé lượt và vé tháng (US-02)", description = "Lấy số lượng vé đã bán trong ngày cho một tuyến cụ thể. Quyền: OWNER")
    public ResponseEntity<TicketStatisticsResponse> getStatistics(
            @RequestParam Integer routeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ResponseEntity.ok(ticketService.getTicketStatistics(routeId, date));
    }
}
