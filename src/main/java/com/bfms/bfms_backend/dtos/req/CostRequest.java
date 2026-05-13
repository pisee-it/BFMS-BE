package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.CostType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Yêu cầu tạo/cập nhật chi phí vận hành")
public record CostRequest(
        @Schema(description = "ID tuyến xe liên quan", example = "1")
        @NotNull(message = "ID tuyến xe không được để trống")
        Integer routeId,

        @Schema(description = "Ngày ghi nhận chi phí", example = "2026-05-13")
        @NotNull(message = "Ngày chi phí không được để trống")
        LocalDate costDate,

        @Schema(description = "Loại chi phí", example = "FUEL")
        @NotNull(message = "Loại chi phí không được để trống")
        CostType type,

        @Schema(description = "Số tiền chi phí", example = "500000")
        @NotNull(message = "Số tiền không được để trống")
        @Min(value = 0, message = "Số tiền không được nhỏ hơn 0")
        BigDecimal amount,

        @Schema(description = "Mô tả chi tiết", example = "Đổ xăng cho xe 29B-12345")
        String description
) {
}
