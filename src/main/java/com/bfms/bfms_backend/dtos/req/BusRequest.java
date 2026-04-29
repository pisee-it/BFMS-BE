package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.BusStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu tạo hoặc cập nhật xe buýt")
public record BusRequest(
        @Schema(description = "ID của tuyến xe mà xe này thuộc về", example = "1")
        @NotNull(message = "Tuyến xe không được để trống")
        Integer routeId,

        @Schema(description = "Dòng xe/Mẫu xe", example = "Hino City")
        @NotBlank(message = "Mẫu xe không được để trống")
        String busModel,

        @Schema(description = "Hãng sản xuất", example = "Hino")
        @NotBlank(message = "Nhà sản xuất không được để trống")
        String manufacturer,

        @Schema(description = "Sức chứa (số ghế)", example = "45")
        @NotNull(message = "Sức chứa không được để trống")
        @Min(value = 0, message = "Sức chứa phải lớn hơn hoặc bằng 0")
        Integer capacity,

        @Schema(description = "Năm sản xuất", example = "2023")
        @NotNull(message = "Năm sản xuất không được để trống")
        @Min(value = 1900, message = "Năm sản xuất không hợp lệ")
        Integer yom,

        @Schema(description = "Biển số xe (duy nhất)", example = "29B-123.45")
        @NotBlank(message = "Biển số xe không được để trống")
        String licensePlate,

        @Schema(description = "Trạng thái hoạt động của xe")
        @NotNull(message = "Trạng thái xe không được để trống")
        BusStatus status,

        @Schema(description = "Xe có đang mang quảng cáo không", example = "false")
        @NotNull(message = "Thông tin quảng cáo không được để trống")
        Boolean isAdvertised
) {
}
