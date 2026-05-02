package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.bfms.bfms_backend.entity.BusStatus;

@Schema(description = "Phản hồi thông tin chi tiết xe buýt")
public record BusResponse(
        @Schema(description = "ID của xe", example = "1")
        Integer id,

        @Schema(description = "ID của tuyến xe hiện tại", example = "5")
        Integer routeId,

        @Schema(description = "Số hiệu tuyến", example = "01")
        String routeNumber,

        @Schema(description = "Dòng xe/Mẫu xe", example = "Hino City")
        String busModel,

        @Schema(description = "Hãng sản xuất", example = "Hino")
        String manufacturer,

        @Schema(description = "Sức chứa (số ghế)", example = "45")
        Integer capacity,

        @Schema(description = "Năm sản xuất", example = "2023")
        Integer yom,

        @Schema(description = "Biển số xe", example = "29B-123.45")
        String licensePlate,

        @Schema(description = "Trạng thái xe (ACTIVE, INACTIVE, MAINTENANCE, SOLD)")
        BusStatus status,

        @Schema(description = "Đang có dán quảng cáo hay không", example = "false")
        Boolean isAdvertised
) {
}
