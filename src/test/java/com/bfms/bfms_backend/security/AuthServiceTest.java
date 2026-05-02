package com.bfms.bfms_backend.security;

import com.bfms.bfms_backend.dtos.req.LoginRequest;
import com.bfms.bfms_backend.dtos.req.RefreshTokenRequest;
import com.bfms.bfms_backend.dtos.res.AuthResponse;
import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.RefreshToken;
import com.bfms.bfms_backend.entity.Role;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.service.AuditService;
import com.bfms.bfms_backend.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private AppUser mockUser;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("admin", "password");
        mockUser = new AppUser();
        mockUser.setUsername("admin");
        mockUser.setRole(Role.ADMIN);
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // 1. Mock Authentication
        Authentication authentication = mock(Authentication.class);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ADMIN")))
                .when(authentication).getAuthorities();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // 2. Mock JWT & Refresh Token
        when(jwtUtil.generateToken("admin")).thenReturn("mock-access-token");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("mock-refresh-token");
        when(refreshTokenService.createRefreshToken("admin")).thenReturn(refreshToken);

        // 3. Execute
        AuthResponse response = authService.login(loginRequest);

        // 4. Verify
        assertNotNull(response);
        assertEquals("mock-access-token", response.accessToken());
        assertEquals("mock-refresh-token", response.refreshToken());
        assertEquals("ADMIN", response.role());
        verify(auditService).log(eq("LOGIN_SUCCESS"), anyString());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        // 1. Mock failure
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        // 2. Execute & Verify
        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
        verify(auditService).log(eq("LOGIN_FAILED"), anyString());
    }

    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenTokenIsValid() {
        // 1. Mock Refresh Token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid-refresh-token");
        refreshToken.setUser(mockUser);

        when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.generateToken("admin")).thenReturn("new-access-token");

        // 2. Execute
        AuthResponse response = authService.refreshToken(new RefreshTokenRequest("valid-refresh-token"));

        // 3. Verify
        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals("ROLE_ADMIN", response.role());
    }

    @Test
    void refreshToken_ShouldThrowException_WhenTokenIsNotFound() {
        // 1. Mock not found
        when(refreshTokenService.findByToken("invalid-token")).thenReturn(Optional.empty());

        // 2. Execute & Verify
        AppException exception = assertThrows(AppException.class, () -> authService.refreshToken(new RefreshTokenRequest("invalid-token")));
        assertEquals(ErrorCode.REFRESH_TOKEN_NOT_FOUND, exception.getErrorCode());
    }
}
