package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.bfms.bfms_backend.entity.AdAssignmentStatus;

@Schema(description = "Phản hồi thông tin phân bổ quảng cáo")
public record AdAssignmentResponse(
        @Schema(description = "ID của bản ghi phân bổ", example = "1")
        Integer id,

        @Schema(description = "ID của hợp đồng quảng cáo liên quan", example = "5")
        Integer adContractId,

        @Schema(description = "ID của xe buýt được gán", example = "10")
        Integer busId,

        @Schema(description = "Biển số xe", example = "29B-123.45")
        String licensePlate,

        @Schema(description = "Trạng thái phân bổ (ACTIVE, REMOVED)")
        AdAssignmentStatus status,

        @Schema(description = "Cảnh báo hợp đồng hết hạn cần chú ý", example = "false")
        Boolean needsAttention
) {}
