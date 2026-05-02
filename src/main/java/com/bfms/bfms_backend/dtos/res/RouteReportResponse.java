package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Báo cáo tài chính chi tiết theo từng tuyến xe")
public record RouteReportResponse(
        @Schema(description = "ID của tuyến xe", example = "1")
        Integer routeId,

        @Schema(description = "Tên tuyến xe", example = "Gia Lâm - Yên Nghĩa")
        String routeName,

        @Schema(description = "Số hiệu tuyến", example = "01")
        String routeNumber,

        @Schema(description = "Doanh thu vé trên tuyến", example = "45000000")
        BigDecimal totalTicketRevenue,

        @Schema(description = "Doanh thu quảng cáo trên tuyến", example = "10000000")
        BigDecimal totalAdRevenue,

        @Schema(description = "Tổng lượng khách trên tuyến", example = "5000")
        Integer totalPassengers,

        @Schema(description = "Tổng thuế trên tuyến", example = "5500000")
        BigDecimal taxDeduction,

        @Schema(description = "Lợi nhuận ròng của tuyến", example = "49500000")
        BigDecimal netProfit,

        @Schema(description = "Ngày bắt đầu kỳ báo cáo")
        LocalDate startDate,

        @Schema(description = "Ngày kết thúc kỳ báo cáo")
        LocalDate endDate
) {}
