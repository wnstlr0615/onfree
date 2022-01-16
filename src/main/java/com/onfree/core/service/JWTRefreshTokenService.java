package com.onfree.core.service;

import com.onfree.core.entity.JWTRefreshToken;
import com.onfree.core.repository.JWTRefreshTokenRepository;
import com.onfree.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JWTRefreshTokenService {
    private final JWTRefreshTokenRepository jwtRefreshTokenRepository;
    private final StringRedisTemplate redisTemplate;
    @Transactional
    public void saveRefreshToken(String username, String refreshToken){
        jwtRefreshTokenRepository.save(
                createJWTRefreshToken(username, refreshToken)
        );
    }
    @Transactional
    public void updateOrSaveRefreshToken(String username, String token){
        final Optional<JWTRefreshToken> optionalRefreshEntity = jwtRefreshTokenRepository.findById(username);
        if(optionalRefreshEntity.isEmpty()){
            saveRefreshToken(username, token);
        }else{
            optionalRefreshEntity.get().updateToken(token);
        }
    }



    private JWTRefreshToken createJWTRefreshToken(String username, String refreshToken) {
        return JWTRefreshToken.builder()
                .userName(username)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void deleteTokenByUsername(String username) {
        if(jwtRefreshTokenRepository.countByUserName(username) != 0){
            jwtRefreshTokenRepository.deleteById(username);
        }
    }

    @Transactional
    public void deleteTokenByRefreshToken(String token) {
        if(jwtRefreshTokenRepository.countByUserName(token) != 0){
            jwtRefreshTokenRepository.deleteByRefreshToken(token);
        }
    }

    public boolean isEmptyRefreshToken(String refreshToken) {
        return jwtRefreshTokenRepository.countByRefreshToken(refreshToken) == 0;
    }

    /*public String findById(String username) {
        return jwtRefreshTokenRepository.findById(username);
    }*/
}
