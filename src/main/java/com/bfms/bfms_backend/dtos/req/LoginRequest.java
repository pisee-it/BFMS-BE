package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
                @NotBlank(message = "Tên đăng nhập không được để trống") String username,

                @NotBlank(message = "Mật khẩu không được để trống") String password) {
}
