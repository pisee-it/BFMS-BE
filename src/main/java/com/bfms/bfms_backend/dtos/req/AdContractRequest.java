package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AdContractRequest(
                @NotNull(message = "ID công ty không được để trống") Integer companyId,

                @NotNull(message = "ID tuyến xe không được để trống") Integer routeId,

                @NotNull(message = "Ngày bắt đầu không được để trống") LocalDate startDate,

                @NotNull(message = "Ngày kết thúc không được để trống") LocalDate endDate,

                @NotNull(message = "Giá tiền mỗi xe không được để trống") @Min(value = 0, message = "Giá tiền không được âm") BigDecimal pricePerBus,

                @NotNull(message = "Số lượng xe không được để trống") @Min(value = 1, message = "Số lượng xe phải ít nhất là 1") Integer busQuantity,

                @NotBlank(message = "Đường dẫn file hợp đồng không được để trống") String contractFileUrl) {
}
