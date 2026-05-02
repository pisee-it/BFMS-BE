package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.RefreshToken;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.repository.AppUserRepository;
import com.bfms.bfms_backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AppUserRepository appUserRepository;

    private RefreshTokenServiceImpl refreshTokenService;

    private final long EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, appUserRepository, EXPIRATION_MS);
    }

    @Test
    void createRefreshToken_ShouldReturnToken_WhenUserExists() {
        // 1. Mock User
        AppUser user = new AppUser();
        user.setUsername("testuser");
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        // 2. Execute
        RefreshToken result = refreshTokenService.createRefreshToken("testuser");

        // 3. Verify
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_ShouldReturnToken_WhenNotExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);
    }

    @Test
    void verifyExpiration_ShouldThrowException_WhenExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(60));

        AppException exception = assertThrows(AppException.class, () -> refreshTokenService.verifyExpiration(token));
        assertEquals(ErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
        verify(refreshTokenRepository).delete(token);
    }
}
