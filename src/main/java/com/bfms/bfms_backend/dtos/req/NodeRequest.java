package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record NodeRequest(
        @NotNull(message = "Số nốt không được để trống")
        @Min(value = 1, message = "Số nốt phải lớn hơn hoặc bằng 1")
        Integer nodeNumber,

        @NotNull(message = "Ngày thực hiện không được để trống")
        LocalDate executionDate,

        String description
) {
}
