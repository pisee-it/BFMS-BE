package com.bfms.bfms_backend.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Yêu cầu cấp mới access token từ refresh token")
public record RefreshTokenRequest(
        @Schema(description = "Chuỗi refresh token được cấp khi login", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "Refresh token không được để trống")
        String refreshToken
) {
}
