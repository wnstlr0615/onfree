package com.onfree.config.security.filter;

import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.exception.LoginException;
import com.onfree.config.security.handler.CustomAuthenticationEntryPoint;
import com.onfree.config.security.CustomUserDetail;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.core.entity.user.User;
import com.onfree.common.model.VerifyResult;
import com.onfree.core.service.LoginService;
import com.onfree.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static com.onfree.common.constant.SecurityConstant.*;

@Slf4j
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {
    private final CustomUserDetailService userDetailService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JWTUtil jwtUtil;
    private final LoginService loginService;

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
                    oldRefreshToken = getRefreshCookieValue(request);
                    if(loginService.isEmptyRefreshToken(username, oldRefreshToken)){
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
                    oldRefreshToken = getRefreshCookieValue(request);
                    if(loginService.isEmptyRefreshToken(username, oldRefreshToken) || tokenIsExpired(oldRefreshToken)){ //DB에 토큰이 없거나 토큰이 유효성이 지난 경우
                        log.info("accessToken expired && refreshToken empty  - username : {}", username);
                        clearToken(response, username);
                        filterChain.doFilter(request,response);
                        return;
                    }
                    log.info("accessToken and RefreshToken reissue - {}", username);
                    tokenCookieReset(response);
                    accessTokenReissue(response, user);
                    refreshTokenReissue(response, username, user);
                    loginSuccess(customUserDetail);
                } catch (LoginException e) {
                    clearToken(response, username);
                }
            }
        }catch (LoginException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request,response,e);
            return;
        }
        filterChain.doFilter(request,response);
    }

    private void accessTokenReissue(HttpServletResponse response, User user) {
        response.addCookie(
                createAccessTokenCookie(user)
        );
    }

    private Cookie createAccessTokenCookie(User user) {
        return createTokenCookie(
                ACCESS_TOKEN,
                createAccessToken(user),
                jwtUtil.getAccessTokenExpiredTime()
        );
    }

    private String createAccessToken(User user) {
        return jwtUtil.createAccessToken(user);
    }

    private void refreshTokenReissue(HttpServletResponse response, String username, User user) {
        final String refreshToken = createRefreshToken(user);
        addNewRefreshTokenCookie(response,
                createRefreshTokenCookie(refreshToken)
        );
        log.info("refreshToken reissue - username : {}", username);
        loginService.saveRefreshToken(username, refreshToken);
    }

    private void loginSuccess(CustomUserDetail customUserDetail) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetail.getUser(), null, customUserDetail.getAuthorities()));
    }

    private boolean tokenIsExpired(String oleRefreshToken) {
        return !jwtUtil.verify(oleRefreshToken).isResult();
    }

    private void clearToken(HttpServletResponse response, String username) {
        tokenCookieReset(response);
        deleteTokenFromRedis(username);
    }

    private void deleteTokenFromRedis(String username) {
            loginService.deleteRefreshTokenByUsername(username);
    }

    private void tokenCookieReset(HttpServletResponse response) {
        response.addCookie(
                createTokenCookie(ACCESS_TOKEN, "",0)
        );
        response.addCookie(
                createTokenCookie(REFRESH_TOKEN, "",0)
        );
    }
    private void addNewRefreshTokenCookie(HttpServletResponse response, Cookie refreshTokenCookie) {
        response.addCookie(
                refreshTokenCookie
            );
    }

    private Cookie createRefreshTokenCookie(String newFreshToken) {
        return createTokenCookie(
                REFRESH_TOKEN,
                newFreshToken,
                jwtUtil.getAccessTokenExpiredTime()
        );
    }

    private String createRefreshToken(User user){
        return jwtUtil.createRefreshToken(
                user
        );
    }

    private CustomUserDetail getCustomUserDetail(String username) {
        return (CustomUserDetail) userDetailService.loadUserByUsername(username);
    }

    private String getRefreshCookieValue(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies() != null ? request.getCookies() : new Cookie[]{};
        final Cookie refreshCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(REFRESH_TOKEN)).findFirst()
                .orElseThrow(() -> new LoginException(LoginErrorCode.NOT_FOUND_REFRESH_TOKEN));
        if(!StringUtils.hasText(refreshCookie.getValue())){
            throw new LoginException(LoginErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }
        return refreshCookie.getValue();
    }

    private Cookie createTokenCookie(String cookieName, String token, long maxAge) {
        final Cookie tokenCookie = new Cookie(cookieName, token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge((int) maxAge);
        return tokenCookie;
    }
}