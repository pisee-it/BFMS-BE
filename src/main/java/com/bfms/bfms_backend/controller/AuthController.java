package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.res.AuthResponse;
import com.bfms.bfms_backend.dtos.req.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bfms.bfms_backend.security.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1. Endpoint xử lý login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // 2. Gọi service xử lý và trả về mã 200 OK kèm Token
        return ResponseEntity.ok(authService.login(request));
    }
}
