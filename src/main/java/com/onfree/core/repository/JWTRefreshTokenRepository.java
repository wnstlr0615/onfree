package com.onfree.core.repository;

import com.onfree.core.entity.JWTRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JWTRefreshTokenRepository extends JpaRepository<JWTRefreshToken, String> {
    int countByUserName(String username);
    int countByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
}
