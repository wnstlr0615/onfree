package com.onfree.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.exception.LoginException;
import com.onfree.core.entity.user.User;
import com.onfree.common.model.VerifyResult;
import com.onfree.common.properties.JWTProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Slf4j
@RequiredArgsConstructor
@Component
public class JWTUtil {
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String ACCESS_TOKEN = "accessToken";

    private final JWTProperties jwtProperties;

    public  String createAccessToken(@NonNull User user){
        return createAccessToken(user, 60 * jwtProperties.getAccessTokenExpiredTime().getSeconds());
    }

    public  String createAccessToken(@NonNull User user, Long tokenExpiredSecond){
        Timestamp expirationTime = Timestamp.valueOf(LocalDateTime.now().plusSeconds(tokenExpiredSecond));
        return JWT.create()
                .withExpiresAt(expirationTime)
                .withSubject(user.getEmail())
                .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
    }

    public  String createRefreshToken(@NonNull User user){
        return createRefreshToken(user, jwtProperties.getRefreshTokenExpiredTime().getSeconds());
    }

    public  String createRefreshToken(@NonNull User user, Long tokenExpiredDay){
        Timestamp expirationTime = Timestamp.valueOf(LocalDateTime.now().plusSeconds(tokenExpiredDay));
        return JWT.create()
                .withExpiresAt(expirationTime)
                .withSubject(user.getEmail())
                .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
    }

    public  VerifyResult verify(@NonNull String token) {
        if(!StringUtils.hasText(token)){
            throw new LoginException(LoginErrorCode.INPUT_WRONG_TOKEN);
        }
        DecodedJWT decodedJWT ;
        try {
             decodedJWT = JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build().verify(token);
        } catch (TokenExpiredException e) {
            decodedJWT = JWT.decode(token);
            return VerifyResult.expired(decodedJWT.getSubject());
        } catch (JWTVerificationException e) {
            throw new LoginException(LoginErrorCode.INPUT_WRONG_TOKEN);
        }
        return VerifyResult.success(decodedJWT.getSubject());
    }

    public String getUsername(String token){
        try {
            return JWT.decode(token)
                    .getSubject();
        } catch (JWTDecodeException e) {
            throw new LoginException(LoginErrorCode.INPUT_WRONG_TOKEN);
        }
    }

    public Long getAccessTokenExpiredTime(){
        return jwtProperties.getAccessTokenExpiredTime().getSeconds();
    }
    public Long getRefreshTokenExpiredTime(){
        return jwtProperties.getRefreshTokenExpiredTime().getSeconds();
    }
}
