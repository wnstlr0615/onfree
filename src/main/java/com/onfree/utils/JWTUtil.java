package com.onfree.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.onfree.config.error.code.LoginErrorCode;
import com.onfree.config.error.exception.LoginException;
import com.onfree.core.entity.user.User;
import com.onfree.core.model.VerifyResult;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Slf4j
public class JWTUtil {
    //TODO 프로퍼티로 처리 예정
    private static final Algorithm algorithm=Algorithm.HMAC512("made onFree by joon");
    public static final String USER_ID = "uid";
    public static final String ONFREE_COM = "onfree.com";
    public static final Long TOKEN_EXPIRED_TIME = 60L;

    public static String createToken(@NonNull User user){
        Timestamp expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(TOKEN_EXPIRED_TIME));
        return JWT.create()
                .withExpiresAt(expirationTime)
                .withSubject(user.getEmail())
                .withIssuer(ONFREE_COM)
                .withClaim(USER_ID, user.getUserId())
                .sign(algorithm);
    }
    public static String createToken(@NonNull User user, Long tokenExpiredSecond){
        Timestamp expirationTime = Timestamp.valueOf(LocalDateTime.now().plusSeconds(tokenExpiredSecond));
        return JWT.create()
                .withExpiresAt(expirationTime)
                .withSubject(user.getEmail())
                .withIssuer(ONFREE_COM)
                .withClaim(USER_ID, user.getUserId())
                .sign(algorithm);
    }

    public static VerifyResult verify(@NonNull String token) {
        if(!StringUtils.hasText(token)){
            return null;
        }
        DecodedJWT decodedJWT ;
        try {
             decodedJWT = JWT.require(algorithm).build().verify(token);
        } catch (TokenExpiredException e) {
            throw new LoginException(LoginErrorCode.TOKEN_IS_EXPIRED);
        } catch (JWTVerificationException e) {
            throw new LoginException(LoginErrorCode.INPUT_WRONG_TOKEN);
        }
        return VerifyResult.builder()
                .result(true)
                .username(decodedJWT.getSubject())
                .build();
    }
}
