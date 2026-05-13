package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Phản hồi thông tin nốt xe")
public record NodeResponse(
        @Schema(description = "ID của nốt xe", example = "1")
        Integer id,

        @Schema(description = "Tên tuyến (ví dụ: Tuyến 01)", example = "Tuyến 01")
        String routeName,

        @Schema(description = "Số thứ tự nốt trong ngày", example = "1")
        Integer nodeNumber,

        @Schema(description = "Ngày thực hiện", example = "2026-05-02")
        LocalDate executionDate,

        @Schema(description = "Tổng lượng khách dự kiến/thực tế", example = "60")
        Integer totalPassengers,

        @Schema(description = "Ghi chú thêm về lượt chạy", example = "Nốt tăng cường")
        String description,

        @Schema(description = "Danh sách các ca chạy trong nốt này")
        List<ShiftResponse> shifts
) {}
