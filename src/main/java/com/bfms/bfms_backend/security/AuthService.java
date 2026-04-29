package com.bfms.bfms_backend.security;

import com.bfms.bfms_backend.dtos.req.RefreshTokenRequest;
import com.bfms.bfms_backend.dtos.res.AuthResponse;
import com.bfms.bfms_backend.dtos.req.LoginRequest;
import com.bfms.bfms_backend.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(LoginRequest request) {
        // Xác thực username/password thông qua AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        // Lấy thông tin Role từ UserDetails sau khi xác thực thành công
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("GUEST");

        // Tạo access token thông qua JwtUtil
        String accessToken = jwtUtil.generateToken(request.username());

        // Tạo refresh token thông qua RefreshTokenService
        String refreshToken = refreshTokenService.createRefreshToken(request.username()).getToken();

        // Trả về DTO chứa token và role
        return new AuthResponse(accessToken, refreshToken, role);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(token -> {
                    String username = token.getUser().getUsername();
                    String accessToken = jwtUtil.generateToken(username);
                    String role = token.getUser().getRole().name();
                    return new AuthResponse(accessToken, token.getToken(), "ROLE_" + role);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong hệ thống."));
    }
}
