package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Yêu cầu tạo hoặc cập nhật công ty quảng cáo")
public record AdCompanyRequest(
        @Schema(description = "Tên công ty quảng cáo", example = "Công ty CP Truyền thông Alpha")
        @NotBlank(message = "Tên công ty không được để trống")
        String name,

        @Schema(description = "Mã số thuế của công ty (duy nhất)", example = "0101234567")
        @NotBlank(message = "Mã số thuế không được để trống")
        String taxCode,

        @Schema(description = "Thông tin liên hệ (SĐT, Email, Địa chỉ)", example = "0988.123.456 - alpha@media.vn")
        @NotBlank(message = "Thông tin liên hệ không được để trống")
        String contact
) {}
