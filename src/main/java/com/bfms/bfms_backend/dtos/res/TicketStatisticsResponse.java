package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Thống kê vé bán ra theo tuyến và ngày")
public record TicketStatisticsResponse(
    @Schema(description = "ID của tuyến xe", example = "1")
    Integer routeId,

    @Schema(description = "Tên tuyến xe", example = "Gia Lâm - Yên Nghĩa")
    String routeName,

    @Schema(description = "Ngày báo cáo")
    LocalDate reportDate,

    @Schema(description = "Tổng số vé lượt bán được", example = "450")
    Integer singleTicketCount,

    @Schema(description = "Tổng số khách dùng vé tháng", example = "150")
    Integer monthlyTicketCount,

    @Schema(description = "Tổng lượt khách (vé lượt + vé tháng)", example = "600")
    Integer totalPassengers,

    @Schema(description = "Tổng doanh thu từ vé lượt (VNĐ)", example = "3600000")
    BigDecimal revenueSingleTickets
) {
}
