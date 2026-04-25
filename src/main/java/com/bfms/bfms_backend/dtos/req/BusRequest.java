package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.BusStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusRequest(
        @NotNull(message = "Tuyến xe không được để trống")
        Integer routeId,

        @NotBlank(message = "Mẫu xe không được để trống")
        String busModel,

        @NotBlank(message = "Nhà sản xuất không được để trống")
        String manufacturer,

        @NotNull(message = "Sức chứa không được để trống")
        @Min(value = 0, message = "Sức chứa phải lớn hơn hoặc bằng 0")
        Integer capacity,

        @NotNull(message = "Năm sản xuất không được để trống")
        @Min(value = 1900, message = "Năm sản xuất không hợp lệ")
        Integer yom,

        @NotBlank(message = "Biển số xe không được để trống")
        String licensePlate,

        @NotNull(message = "Trạng thái xe không được để trống")
        BusStatus status,

        @NotNull(message = "Thông tin quảng cáo không được để trống")
        Boolean isAdvertised
) {
}
