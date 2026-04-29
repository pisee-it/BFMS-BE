package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Yêu cầu đăng nhập")
public record LoginRequest(
        @Schema(description = "Tên đăng nhập", example = "admin") @NotBlank(message = "Tên đăng nhập không được để trống") String username,

        @Schema(description = "Mật khẩu", example = "123456") @NotBlank(message = "Mật khẩu không được để trống") String password) {
}
