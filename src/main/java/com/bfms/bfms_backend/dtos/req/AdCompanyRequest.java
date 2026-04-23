package com.bfms.bfms_backend.dtos.req;

public record AdCompanyRequest(
        String name,
        String taxCode,
        String contact
) {}
