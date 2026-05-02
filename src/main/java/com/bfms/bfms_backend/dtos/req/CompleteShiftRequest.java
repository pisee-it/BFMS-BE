package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu hoàn thành chuyến xe (cập nhật số vé)")
public record CompleteShiftRequest(
                @Schema(description = "Tổng số vé lượt bán được trong ca", example = "45")
                @NotNull(message = "Số lượng vé lượt không được để trống") @Min(value = 0, message = "Số lượng vé lượt phải lớn hơn hoặc bằng 0") Integer total_single_tickets,

                @Schema(description = "Tổng số khách dùng vé tháng trong ca", example = "15")
                @NotNull(message = "Số lượng vé tháng không được để trống") @Min(value = 0, message = "Số lượng vé tháng phải lớn hơn hoặc bằng 0") Integer total_monthly_tickets
) {
}
