package com.bfms.bfms_backend.dtos.req;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdContractRequest(
        Integer companyId,
        Integer routeId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal pricePerBus,
        Integer busQuantity,
        String contractFileUrl
) {}
