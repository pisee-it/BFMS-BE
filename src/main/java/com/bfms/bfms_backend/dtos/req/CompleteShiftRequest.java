package com.bfms.bfms_backend.dtos.req;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CompleteShiftRequest(
        @NotNull @Min(0) Integer total_single_tickets,
        @NotNull @Min(0) Integer total_monthly_tickets // Staff nhập tay số lượng
) {}
