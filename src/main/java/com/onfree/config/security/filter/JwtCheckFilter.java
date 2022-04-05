package com.onfree.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.exception.LoginException;
import com.onfree.common.model.VerifyResult;
import com.onfree.config.security.CustomUserDetail;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.config.security.dto.JwtLoginResponse;
import com.onfree.config.security.handler.CustomAuthenticationEntryPoint;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.LoginService;
import com.onfree.utils.CookieUtil;
import com.onfree.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.onfree.common.constant.SecurityConstant.*;

@Slf4j
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {
    private final CustomUserDetailService userDetailService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JWTUtil jwtUtil;
    private final LoginService loginService;
    private final CookieUtil cookieUtil;
    private final ObjectMapper mapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(BEARER.length()).trim();
        try {
            final VerifyResult verify = jwtUtil.verify(token);
            final String username = jwtUtil.getUsername(token);
            final String oldRefreshToken;
            final CustomUserDetail customUserDetail = getCustomUserDetail(username);
            final User user = customUserDetail.getUser();
            if (verify.isResult()) { //accessToken 이 유효한 경우
                try {
                    oldRefreshToken = getCookieValue(request, REFRESH_TOKEN);
                    if(isWrongRefreshToken(username, oldRefreshToken)){// refreshToken 이 없거나 변조된 경우 (로그아웃 )
                        log.info("refresh token empty - username : {} ", username);
                        clearToken(response, username);
                        filterChain.doFilter(request,response);
                        return;
                    }
                    if(tokenIsExpired(oldRefreshToken)){  // refreshToken 이 만료된 경우
                        log.info("accessToken success but refreshToken expired");
                        refreshTokenReissue(response, username, user);
                     }
                    log.info("accessToken success login success - username : {}", username);
                    loginSuccess(customUserDetail);
                } catch (LoginException exception) { //refreshToken 이 없는 경우
                    refreshTokenReissue(response, username, user);
                    loginSuccess(customUserDetail);
                }
            }else{//accessToken 이 유효하지 않은 경우
                try {
                    log.info("accessToken expired - usernaem : {}", username);
                    oldRefreshToken = getCookieValue(request, REFRESH_TOKEN);
                    if(isWrongRefreshToken(username, oldRefreshToken) || tokenIsExpired(oldRefreshToken)){ //DB에 토큰이 없거나 토큰이 유효성이 지난 경우
                        log.info("accessToken expired && refreshToken empty  - username : {}", username);
                        clearToken(response, username);
                        filterChain.doFilter(request,response);
                        return;
                    }
                    log.info("accessToken and RefreshToken reissue - {}", username);
                    tokenCookieReset(response);
                    reissueTokenResponse(response, user);
                    return;
                } catch (LoginException e) {
                    clearToken(response, username);
                    throw e;
                }
            }
        }catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isWrongRefreshToken(String username, String oldRefreshToken) {
        return loginService.isWrongRefreshToken(username, oldRefreshToken);

    }

    private String accessTokenReissue(HttpServletResponse response, User user) {
        String newAccessToken = createAccessToken(user);
        response.addCookie(
                createAccessTokenCookie(
                        newAccessToken
                )
        );
        return newAccessToken;
    }

    private String createAccessToken(User user) {
        return jwtUtil.createAccessToken(user);
    }

    private Cookie createAccessTokenCookie(String accessToken) {
        return cookieUtil.createCookie(
                ACCESS_TOKEN,
                accessToken,
                (int) jwtUtil.getAccessTokenExpiredTime()
        );
    }

    private String refreshTokenReissue(HttpServletResponse response, String username, User user) {
        log.info("refreshToken reissue - username : {}", username);
        final String refreshToken = createRefreshToken(user);
        response.addCookie(
                createRefreshTokenCookie(refreshToken)
        );
        loginService.saveRefreshToken(username, refreshToken);
        return refreshToken;
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        return cookieUtil.createCookie(
                REFRESH_TOKEN,
                refreshToken,
                (int) jwtUtil.getRefreshTokenExpiredTime()
        );
    }

    private String createRefreshToken(User user) {
        return jwtUtil.createRefreshToken(user);
    }

    private void loginSuccess(CustomUserDetail customUserDetail) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetail.getUser(), null, customUserDetail.getAuthorities())
        );
    }

    private boolean tokenIsExpired(String token) {
        return !jwtUtil.verify(token).isResult();
    }

    private void clearToken(HttpServletResponse response, String username) {
        tokenCookieReset(response);
        deleteRefreshTokenFromRedis(username);
    }

    private CustomUserDetail getCustomUserDetail(String username) {
        return (CustomUserDetail) userDetailService.loadUserByUsername(username);
    }

    private void deleteRefreshTokenFromRedis(String username) {
        loginService.deleteRefreshTokenByUsername(username);
    }

    private void tokenCookieReset(HttpServletResponse response) {
        response.addCookie(
                createResetCookie(ACCESS_TOKEN)
        );
        response.addCookie(
                createResetCookie(REFRESH_TOKEN)
        );
    }

    private Cookie createResetCookie(String accessToken) {
        return cookieUtil.resetCookie(accessToken);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        final Cookie cookie = cookieUtil.getCookie(request, name);
        if(cookie == null || !StringUtils.hasText(cookie.getValue())){
            throw new LoginException(LoginErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }
        return cookie.getValue();
    }

    private void reissueTokenResponse(HttpServletResponse response, User user) throws IOException {
        final JwtLoginResponse jwtLoginResponse = getJWTLoginResponse(user);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(
                mapper.writeValueAsString(
                        jwtLoginResponse
                )
        );
        response.setHeader("Authorization", BEARER+" " + jwtLoginResponse.getAccessToken());
        response.addCookie(
                cookieUtil.createCookie(ACCESS_TOKEN, jwtLoginResponse.getAccessToken(), (int)jwtUtil.getAccessTokenExpiredTime())
        );
        response.addCookie(
                cookieUtil.createCookie(REFRESH_TOKEN, jwtLoginResponse.getRefreshToken(), (int)jwtUtil.getRefreshTokenExpiredTime())
        );
        loginService.saveRefreshToken(jwtLoginResponse.getUsername(), jwtLoginResponse.getRefreshToken());
    }

    private JwtLoginResponse getJWTLoginResponse(User user) {
        final String accessToken = jwtUtil.createAccessToken(user);
        final String refreshToken = jwtUtil.createRefreshToken(user);
        return JwtLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getEmail())
                .result(true)
                .build();
    }




}