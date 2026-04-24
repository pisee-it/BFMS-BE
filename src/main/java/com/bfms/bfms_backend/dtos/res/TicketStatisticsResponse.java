package com.bfms.bfms_backend.dtos.res;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO trả về thống kê vé theo tuyến và ngày.
 */
public record TicketStatisticsResponse(
    Integer routeId,
    String routeName,
    LocalDate reportDate,
    Integer singleTicketCount,
    Integer monthlyTicketCount,
    Integer totalPassengers,
    BigDecimal revenueSingleTickets
) {
}
