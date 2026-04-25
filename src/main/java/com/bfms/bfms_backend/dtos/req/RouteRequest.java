package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

public record RouteRequest(
                @NotBlank(message = "Số tuyến không được để trống") String routeNumber,

                @NotBlank(message = "Điểm đầu không được để trống") String stopA,

                @NotBlank(message = "Điểm cuối không được để trống") String stopB,

                @NotBlank(message = "Lộ trình không được để trống") String path,

                @NotNull(message = "Khoảng cách AB không được để trống") @Min(value = 0, message = "Khoảng cách không được âm") BigDecimal distanceAB,

                @NotNull(message = "Khoảng cách BA không được để trống") @Min(value = 0, message = "Khoảng cách không được âm") BigDecimal distanceBA,

                @NotNull(message = "Giờ bắt đầu hoạt động không được để trống") LocalTime operationStart,

                @NotNull(message = "Giờ kết thúc hoạt động không được để trống") LocalTime operationEnd,

                @NotNull(message = "Giá vé không được để trống") @Min(value = 0, message = "Giá vé không được âm") BigDecimal price) {
}
