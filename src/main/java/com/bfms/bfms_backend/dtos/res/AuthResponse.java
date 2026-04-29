package com.bfms.bfms_backend.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Phản hồi xác thực chứa cặp token")
public record AuthResponse(
        @Schema(description = "Access Token dùng để gọi các API bảo mật")
        String accessToken,
        @Schema(description = "Refresh Token dùng để làm mới Access Token")
        String refreshToken,
        @Schema(description = "Vai trò của người dùng", example = "ADMIN")
        String role) {
}
