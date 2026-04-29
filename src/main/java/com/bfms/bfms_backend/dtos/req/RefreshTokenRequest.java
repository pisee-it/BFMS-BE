package com.bfms.bfms_backend.dtos.req;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token không được để trống")
        String refreshToken
) {
}
