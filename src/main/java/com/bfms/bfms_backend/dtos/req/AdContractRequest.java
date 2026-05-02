package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Yêu cầu tạo hoặc cập nhật hợp đồng quảng cáo")
public record AdContractRequest(
                @Schema(description = "ID của công ty quảng cáo đối tác", example = "1")
                @NotNull(message = "ID công ty không được để trống") Integer companyId,

                @Schema(description = "ID của tuyến xe áp dụng quảng cáo", example = "5")
                @NotNull(message = "ID tuyến xe không được để trống") Integer routeId,

                @Schema(description = "Ngày bắt đầu hiệu lực hợp đồng", example = "2026-05-01")
                @NotNull(message = "Ngày bắt đầu không được để trống") LocalDate startDate,

                @Schema(description = "Ngày kết thúc hiệu lực hợp đồng", example = "2026-11-01")
                @NotNull(message = "Ngày kết thúc không được để trống") LocalDate endDate,

                @Schema(description = "Đơn giá quảng cáo trên mỗi xe (VNĐ)", example = "5000000")
                @NotNull(message = "Giá tiền mỗi xe không được để trống") @Min(value = 0, message = "Giá tiền không được âm") BigDecimal pricePerBus,

                @Schema(description = "Số lượng xe cam kết trong hợp đồng", example = "10")
                @NotNull(message = "Số lượng xe không được để trống") @Min(value = 1, message = "Số lượng xe phải ít nhất là 1") Integer busQuantity,

                @Schema(description = "Đường dẫn file scan hợp đồng (đã upload)", example = "/api/v1/files/contract_uuid.pdf")
                @NotBlank(message = "Đường dẫn file hợp đồng không được để trống") String contractFileUrl) {
}
