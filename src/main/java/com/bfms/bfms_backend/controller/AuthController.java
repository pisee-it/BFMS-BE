package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.RefreshTokenRequest;
import com.bfms.bfms_backend.dtos.res.AuthResponse;
import com.bfms.bfms_backend.dtos.req.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bfms.bfms_backend.security.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Xác thực (Authentication)", description = "Các API liên quan đến đăng nhập và cấp mới token")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1. Endpoint xử lý login
    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống", description = "Xác thực người dùng và trả về Access Token cùng Refresh Token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // 2. Gọi service xử lý và trả về mã 200 OK kèm Token
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới token", description = "Sử dụng Refresh Token để lấy Access Token mới")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
