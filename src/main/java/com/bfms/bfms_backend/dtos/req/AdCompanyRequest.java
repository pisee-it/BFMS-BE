package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.NotBlank;

public record AdCompanyRequest(
        @NotBlank(message = "Tên công ty không được để trống")
        String name,

        @NotBlank(message = "Mã số thuế không được để trống")
        String taxCode,

        @NotBlank(message = "Thông tin liên hệ không được để trống")
        String contact
) {}
