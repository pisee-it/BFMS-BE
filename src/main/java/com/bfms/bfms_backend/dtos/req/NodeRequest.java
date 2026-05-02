package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Yêu cầu tạo hoặc cập nhật nốt xe (lượt chạy trong ngày)")
public record NodeRequest(
        @Schema(description = "Số thứ tự nốt chạy trong ngày", example = "1")
        @NotNull(message = "Số nốt không được để trống")
        @Min(value = 1, message = "Số nốt phải lớn hơn hoặc bằng 1")
        Integer nodeNumber,

        @Schema(description = "Ngày thực hiện lượt chạy", example = "2026-05-02")
        @NotNull(message = "Ngày thực hiện không được để trống")
        LocalDate executionDate,

        @Schema(description = "Ghi chú thêm về lượt chạy", example = "Nốt tăng cường giờ cao điểm")
        String description
) {
}
