package com.bfms.bfms_backend.dtos.res;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueResponse(
    BigDecimal totalTicketRevenue,
    BigDecimal totalAdRevenue,
    BigDecimal taxDeduction,
    BigDecimal netProfit,
    Integer totalPassengers,
    String timeframe,
    LocalDate date
) {
}
