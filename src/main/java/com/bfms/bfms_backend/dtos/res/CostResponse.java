package com.bfms.bfms_backend.dtos.res;

import com.bfms.bfms_backend.entity.CostType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(description = "Thông tin chi tiết chi phí vận hành")
public record CostResponse(
        @Schema(description = "ID chi phí", example = "1")
        Integer id,
        
        @Schema(description = "ID tuyến xe", example = "1")
        Integer routeId,
        
        @Schema(description = "Số hiệu tuyến xe", example = "01")
        String routeNumber,
        
        @Schema(description = "Ngày chi phí", example = "2026-05-13")
        LocalDate costDate,
        
        @Schema(description = "Loại chi phí", example = "FUEL")
        CostType type,
        
        @Schema(description = "Số tiền", example = "500000")
        BigDecimal amount,
        
        @Schema(description = "Mô tả", example = "Đổ xăng")
        String description,
        
        @Schema(description = "Thời điểm tạo")
        OffsetDateTime createdAt
) {
}
