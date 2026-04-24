package com.bfms.bfms_backend.dtos.res;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO chứa thông tin báo cáo chi tiết cho một tuyến xe.
 */
public record RouteReportResponse(
        Integer routeId,
        String routeName,
        String routeNumber,
        BigDecimal totalTicketRevenue,
        BigDecimal totalAdRevenue,
        Integer totalPassengers,
        BigDecimal taxDeduction,
        BigDecimal netProfit,
        LocalDate startDate,
        LocalDate endDate
) {}
