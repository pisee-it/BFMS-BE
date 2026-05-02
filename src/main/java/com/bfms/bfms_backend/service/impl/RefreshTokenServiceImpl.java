package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.RefreshToken;
import com.bfms.bfms_backend.repository.AppUserRepository;
import com.bfms.bfms_backend.repository.RefreshTokenRepository;
import com.bfms.bfms_backend.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AppUserRepository appUserRepository;
    private final long refreshTokenExpirationMs;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            AppUserRepository appUserRepository,
            @Value("${application.security.jwt.refresh-token.expiration}") long refreshTokenExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.appUserRepository = appUserRepository;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Xóa token cũ của user nếu có (mỗi user chỉ duy trì 1 refresh token mới nhất)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUser(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
    }
}
