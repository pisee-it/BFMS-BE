package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu phân bổ quảng cáo lên xe buýt")
public record AdAssignmentRequest(
        @Schema(description = "ID của hợp đồng quảng cáo", example = "1")
        @NotNull(message = "ID hợp đồng quảng cáo không được để trống")
        Integer adContractId,

        @Schema(description = "ID của xe buýt được gán quảng cáo", example = "10")
        @NotNull(message = "ID xe buýt không được để trống")
        Integer busId
) {}
