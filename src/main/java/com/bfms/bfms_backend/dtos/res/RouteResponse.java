package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalTime;

@Schema(description = "Phản hồi thông tin tuyến xe buýt")
public record RouteResponse(
        @Schema(description = "ID của tuyến", example = "1")
        Integer id,

        @Schema(description = "Số hiệu tuyến", example = "01")
        String routeNumber,

        @Schema(description = "Điểm đầu A", example = "Gia Lâm")
        String stopA,

        @Schema(description = "Điểm đầu B", example = "Yên Nghĩa")
        String stopB,

        @Schema(description = "Lộ trình chi tiết", example = "Nguyễn Văn Cừ - ...")
        String path,

        @Schema(description = "Khoảng cách AB (km)", example = "15.5")
        BigDecimal distanceAB,

        @Schema(description = "Khoảng cách BA (km)", example = "15.8")
        BigDecimal distanceBA,

        @Schema(description = "Giờ mở bến", example = "05:00:00")
        LocalTime operationStart,

        @Schema(description = "Giờ đóng bến", example = "21:00:00")
        LocalTime operationEnd,

        @Schema(description = "Giá vé lượt hiện tại (VNĐ)", example = "8000")
        BigDecimal price
) {
}
