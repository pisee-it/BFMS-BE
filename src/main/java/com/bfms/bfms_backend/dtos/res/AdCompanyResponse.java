package com.bfms.bfms_backend.dtos.res;

public record AdCompanyResponse(
        Integer id,
        String name,
        String taxCode,
        String contact
) {}
