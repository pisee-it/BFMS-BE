package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.bfms.bfms_backend.entity.AdContractStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(description = "Phản hồi thông tin hợp đồng quảng cáo")
public record AdContractResponse(
        @Schema(description = "ID của hợp đồng", example = "1")
        Integer id,

        @Schema(description = "ID của công ty quảng cáo", example = "2")
        Integer companyId,

        @Schema(description = "Tên công ty quảng cáo", example = "Công ty CP Truyền thông Alpha")
        String companyName,

        @Schema(description = "ID của tuyến xe", example = "5")
        Integer routeId,

        @Schema(description = "Số hiệu tuyến", example = "01")
        String routeNumber,

        @Schema(description = "Ngày bắt đầu hiệu lực", example = "2026-05-01")
        LocalDate startDate,

        @Schema(description = "Ngày kết thúc hiệu lực", example = "2026-11-01")
        LocalDate endDate,

        @Schema(description = "Đơn giá mỗi xe (VNĐ)", example = "5000000")
        BigDecimal pricePerBus,

        @Schema(description = "Số lượng xe cam kết", example = "10")
        Integer busQuantity,

        @Schema(description = "Trạng thái phê duyệt (PENDING, APPROVED, PAID, REJECTED, DELETE_REQUESTED)")
        AdContractStatus approvalStatus,

        @Schema(description = "Đường dẫn file hợp đồng", example = "/api/v1/files/contract_uuid.pdf")
        String contractFileUrl,

        @Schema(description = "Thời điểm tạo bản ghi")
        OffsetDateTime createdAt
) {}
