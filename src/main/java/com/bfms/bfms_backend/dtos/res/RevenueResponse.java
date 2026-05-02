package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Phản hồi báo cáo doanh thu")
public record RevenueResponse(
    @Schema(description = "Tổng doanh thu từ bán vé (vé lượt + vé tháng)", example = "150000000")
    BigDecimal totalTicketRevenue,

    @Schema(description = "Tổng doanh thu từ quảng cáo", example = "50000000")
    BigDecimal totalAdRevenue,

    @Schema(description = "Tổng các khoản thuế phải nộp (VAT, TNDN)", example = "20000000")
    BigDecimal taxDeduction,

    @Schema(description = "Lợi nhuận ròng sau thuế", example = "180000000")
    BigDecimal netProfit,

    @Schema(description = "Tổng lượng hành khách", example = "12000")
    Integer totalPassengers,

    @Schema(description = "Khoảng thời gian báo cáo (DAILY, MONTHLY, YEARLY)", example = "DAILY")
    String timeframe,

    @Schema(description = "Ngày báo cáo")
    LocalDate date
) {
}
