package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

@Schema(description = "Yêu cầu tạo hoặc cập nhật tuyến xe buýt")
public record RouteRequest(
                @Schema(description = "Số hiệu tuyến", example = "01")
                @NotBlank(message = "Số tuyến không được để trống") String routeNumber,

                @Schema(description = "Tên bến đầu A", example = "Bến xe Gia Lâm")
                @NotBlank(message = "Điểm đầu không được để trống") String stopA,

                @Schema(description = "Tên bến đầu B", example = "Bến xe Yên Nghĩa")
                @NotBlank(message = "Điểm cuối không được để trống") String stopB,

                @Schema(description = "Mô tả lộ trình chi tiết", example = "Ngô Gia Tự - Nguyễn Văn Cừ - ...")
                @NotBlank(message = "Lộ trình không được để trống") String path,

                @Schema(description = "Khoảng cách chiều đi (km)", example = "15.5")
                @NotNull(message = "Khoảng cách AB không được để trống") @Min(value = 0, message = "Khoảng cách không được âm") BigDecimal distanceAB,

                @Schema(description = "Khoảng cách chiều về (km)", example = "15.8")
                @NotNull(message = "Khoảng cách BA không được để trống") @Min(value = 0, message = "Khoảng cách không được âm") BigDecimal distanceBA,

                @Schema(description = "Giờ mở bến", example = "05:00:00")
                @NotNull(message = "Giờ bắt đầu hoạt động không được để trống") LocalTime operationStart,

                @Schema(description = "Giờ đóng bến", example = "21:00:00")
                @NotNull(message = "Giờ kết thúc hoạt động không được để trống") LocalTime operationEnd,

                @Schema(description = "Giá vé lượt (VNĐ) - Nếu để trống hệ thống sẽ tự tính", example = "8000")
                @NotNull(message = "Giá vé không được để trống") @Min(value = 0, message = "Giá vé không được âm") BigDecimal price) {
}
