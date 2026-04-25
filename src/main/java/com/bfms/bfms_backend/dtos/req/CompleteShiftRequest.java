package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CompleteShiftRequest(
                @NotNull(message = "Số lượng vé lượt không được để trống") @Min(value = 0, message = "Số lượng vé lượt phải lớn hơn hoặc bằng 0") Integer total_single_tickets,

                @NotNull(message = "Số lượng vé tháng không được để trống") @Min(value = 0, message = "Số lượng vé tháng phải lớn hơn hoặc bằng 0") Integer total_monthly_tickets // Staff
                                                                                                                                                                                  // nhập
                                                                                                                                                                                  // tay
                                                                                                                                                                                  // số
                                                                                                                                                                                  // lượng
) {
}
