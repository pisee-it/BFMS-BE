package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.NotNull;

public record AdAssignmentRequest(
        @NotNull(message = "ID hợp đồng quảng cáo không được để trống")
        Integer adContractId,

        @NotNull(message = "ID xe buýt không được để trống")
        Integer busId
) {}
