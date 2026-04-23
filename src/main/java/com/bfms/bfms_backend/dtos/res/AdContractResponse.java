package com.bfms.bfms_backend.dtos.res;

import com.bfms.bfms_backend.entity.AdContractStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record AdContractResponse(
        Integer id,
        Integer companyId,
        String companyName,
        Integer routeId,
        String routeNumber,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal pricePerBus,
        Integer busQuantity,
        AdContractStatus approvalStatus,
        String contractFileUrl,
        OffsetDateTime createdAt
) {}
