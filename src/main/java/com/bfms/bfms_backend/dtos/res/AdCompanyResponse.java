package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Phản hồi thông tin công ty quảng cáo")
public record AdCompanyResponse(
        @Schema(description = "ID của công ty", example = "1")
        Integer id,

        @Schema(description = "Tên công ty", example = "Công ty CP Truyền thông Alpha")
        String name,

        @Schema(description = "Mã số thuế", example = "0101234567")
        String taxCode,

        @Schema(description = "Thông tin liên hệ", example = "0988.123.456 - alpha@media.vn")
        String contact
) {}
