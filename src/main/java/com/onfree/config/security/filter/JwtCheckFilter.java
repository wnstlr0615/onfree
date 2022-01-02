package com.onfree.config.security.filter;

import com.onfree.config.error.code.LoginErrorCode;
import com.onfree.config.error.exception.LoginException;
import com.onfree.config.security.handler.CustomAuthenticationEntryPoint;
import com.onfree.config.security.CustomUserDetail;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.core.entity.user.User;
import com.onfree.core.model.VerifyResult;
import com.onfree.core.service.JWTRefreshTokenService;
import com.onfree.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class JwtCheckFilter extends OncePerRequestFilter {
    public static final String BEARER = "Bearer";
    private final CustomUserDetailService userDetailService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JWTRefreshTokenService jwtRefreshTokenService;
    private final JWTUtil jwtUtil;

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
                    if(jwtRefreshTokenService.isEmptyRefreshToken(oldRefreshToken)){
                        clearToken(response, username);
                        filterChain.doFilter(request,response);
                        return;
                    }
                    if(tokenIsExpired(oldRefreshToken)){  // refreshToken 이 만료된 경우
                        refreshTokenReissue(response, username, user);
                     }
                    loginSuccess(customUserDetail);
                } catch (LoginException exception) { //refreshToken 이 없는 경우
                    refreshTokenReissue(response, username, user);
                    loginSuccess(customUserDetail);
                }
            }else{//accessToken 이 유효하지 않은 경우
                try {
                    oldRefreshToken = getRefreshCookieValue(request);
                    if(jwtRefreshTokenService.isEmptyRefreshToken(oldRefreshToken) || tokenIsExpired(oldRefreshToken)){ //DB에 토큰이 없거나 토큰이 유효성이 지난 경우
                        clearToken(response, username);
                        filterChain.doFilter(request,response);
                        return;
                    }
                    refreshTokenReissue(response, username, user);
                    accessTokenReissue(response, user);
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
                JWTUtil.ACCESS_TOKEN,
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
        jwtRefreshTokenService.updateOrSaveRefreshToken(username, refreshToken);
    }

    private void loginSuccess(CustomUserDetail customUserDetail) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetail.getUser(), null, customUserDetail.getAuthorities()));
    }

    private boolean tokenIsExpired(String oleRefreshToken) {
        return !jwtUtil.verify(oleRefreshToken).isResult();
    }

    private void clearToken(HttpServletResponse response, String username) {
        tokenCookieReset(response);
        deleteTokenFromDB(username);
    }

    private void deleteTokenFromDB(String username) {
            jwtRefreshTokenService.deleteTokenByUsername(username);
    }

    private void tokenCookieReset(HttpServletResponse response) {
        response.addCookie(
                createTokenCookie(JWTUtil.ACCESS_TOKEN, "",0)
        );
        response.addCookie(
                createTokenCookie(JWTUtil.REFRESH_TOKEN, "",0)
        );
    }
    private void addNewRefreshTokenCookie(HttpServletResponse response, Cookie refreshTokenCookie) {
        response.addCookie(
                refreshTokenCookie
            );
    }

    private Cookie createRefreshTokenCookie(String newFreshToken) {
        return createTokenCookie(
                JWTUtil.REFRESH_TOKEN,
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
        final Cookie refreshCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(JWTUtil.REFRESH_TOKEN)).findFirst()
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
