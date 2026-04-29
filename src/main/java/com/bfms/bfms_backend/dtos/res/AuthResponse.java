package com.bfms.bfms_backend.dtos.res;

public record AuthResponse(String accessToken, String refreshToken, String role) {
}
