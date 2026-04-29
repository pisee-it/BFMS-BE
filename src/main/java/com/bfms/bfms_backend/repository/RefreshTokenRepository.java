package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(AppUser user);
}
