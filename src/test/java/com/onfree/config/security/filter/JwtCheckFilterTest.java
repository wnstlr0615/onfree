package com.onfree.config.security.filter;

import com.onfree.config.security.CustomUserDetail;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.core.entity.user.Gender;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.entity.user.Role;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.JWTRefreshTokenService;
import com.onfree.common.properties.JWTProperties;
import com.onfree.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.http.Cookie;
import java.time.Duration;

import static com.onfree.common.constant.SecurityConstant.*;
import static com.onfree.utils.JWTUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = "" +
        "spring.config.location:" +
        "classpath:application.yml" +
        ",classpath:aws.yml")
class JwtCheckFilterTest {
    @MockBean
    JWTProperties jwtProperties;
    @SpyBean
    JWTUtil jwtUtil;
    @MockBean
    JWTRefreshTokenService jwtRefreshTokenService;
    @SpyBean
    JwtCheckFilter jwtCheckFilter;
    @MockBean
    CustomUserDetailService userDetailsService;
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    MockFilterChain mockFilterChain;


    @BeforeEach
    public void initMock() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockFilterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("[요청 성공] AC 토큰이 유효하며 RF 토큰이 유효한 경우")
    public void givenACAndRFToken_whenCheckFilter_thenSuccess() throws Exception {
        //given
        final User normalUser = createNormalUser();

        when(jwtRefreshTokenService.isEmptyRefreshToken(anyString())).thenReturn(false);
        when(jwtProperties.getAccessTokenExpiredTime()).thenReturn(Duration.ofMinutes(30));
        when(jwtProperties.getRefreshTokenExpiredTime()).thenReturn(Duration.ofDays(7));
        when(jwtProperties.getSecretKey()).thenReturn("aaaaa");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(new CustomUserDetail(normalUser));

        final String accessToken = jwtUtil.createAccessToken(normalUser);
        final String refreshToken = jwtUtil.createRefreshToken(normalUser);

        requestSetToken(accessToken, refreshToken);
        //when & then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

        verify(userDetailsService).loadUserByUsername(anyString());
        verify(jwtRefreshTokenService).isEmptyRefreshToken(anyString());
        verify(jwtRefreshTokenService, never()).updateOrSaveRefreshToken(anyString(), anyString());

    }

    private User createNormalUser() {
        return NormalUser.builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("{noop}!Abcderghijk112")
                .gender(Gender.MAN)
                .nickname("온프리짱짱")
                .name("준식")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .profileImage("http://onfree.io/images/123456789")
                .role(Role.NORMAL)
                .build();
    }

    private Cookie createTokenCookie(String cookieName, String token, long maxAge) {
        final Cookie tokenCookie = new Cookie(cookieName, token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setMaxAge((int) maxAge);
        return tokenCookie;
    }

    @Test
    @DisplayName("[요청 성공] AC 토큰은 유효 하나  RF 토큰이 유효하지 않은 경우 - RF 토큰 재발급 후 요청처리")
    public void givenACTokenNotValidRFToken_whenCheckFilter_thenRFTokenReissueAndSuccess() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(jwtRefreshTokenService.isEmptyRefreshToken(anyString())).thenReturn(false);
        when(jwtProperties.getAccessTokenExpiredTime()).thenReturn(Duration.ofHours(1));
        when(jwtProperties.getRefreshTokenExpiredTime()).thenReturn(Duration.ofSeconds(-1));
        when(jwtProperties.getSecretKey()).thenReturn("aaaaa");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(new CustomUserDetail(normalUser));

        final String accessToken = jwtUtil.createAccessToken(normalUser);
        final String refreshToken = jwtUtil.createRefreshToken(normalUser);

        requestSetToken(accessToken, refreshToken);


        //when & then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

        verify(userDetailsService).loadUserByUsername(anyString());
        verify(jwtRefreshTokenService).isEmptyRefreshToken(anyString());
        verify(jwtRefreshTokenService).updateOrSaveRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("[요청 성공] AC 토큰은 유효 하나  RF 토큰이 DB에 존재 하지 않을 경우 - 로그아웃 처리 토큰 쿠키 & 데이터 제거")
    public void givenACTokenNotFoundRFTokenInDB_whenCheckFilter_thenLogout() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(jwtRefreshTokenService.isEmptyRefreshToken(anyString())).thenReturn(true);
        when(jwtProperties.getAccessTokenExpiredTime()).thenReturn(Duration.ofHours(1));
        when(jwtProperties.getRefreshTokenExpiredTime()).thenReturn(Duration.ofDays(7));
        when(jwtProperties.getSecretKey()).thenReturn("aaaaa");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(new CustomUserDetail(normalUser));

        final String accessToken = jwtUtil.createAccessToken(normalUser);
        final String refreshToken = jwtUtil.createRefreshToken(normalUser);

        requestSetToken(accessToken, refreshToken);

        //when & then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getCookie(ACCESS_TOKEN).getValue()).isEqualTo("");
        assertThat(response.getCookie(REFRESH_TOKEN).getValue()).isEqualTo("");

        verify(userDetailsService).loadUserByUsername(anyString());
        verify(jwtRefreshTokenService).isEmptyRefreshToken(anyString());
        verify(jwtRefreshTokenService, never()).updateOrSaveRefreshToken(anyString(), anyString());
        verify(jwtUtil, never()).verify(eq(refreshToken));
    }

    private void requestSetToken(String accessToken, String refreshToken) {
        request.setCookies(
                createTokenCookie(ACCESS_TOKEN, accessToken, jwtUtil.getAccessTokenExpiredTime()),
                createTokenCookie(REFRESH_TOKEN, refreshToken, jwtUtil.getRefreshTokenExpiredTime())
        );
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER + " " + accessToken);
    }

    @Test
    @DisplayName("[요청 성공] AC 토큰은 유효 하나  RF 토큰 쿠키가 존재 하지 않을 경우 - RF 토큰 재발급 후 요청처리")
    public void givenACTokenNotFoundRFTokenCookie_whenCheckFilter_thenRFTokenReissueAndSuccess() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(jwtProperties.getAccessTokenExpiredTime()).thenReturn(Duration.ofHours(1));
        when(jwtProperties.getRefreshTokenExpiredTime()).thenReturn(Duration.ofDays(7));
        when(jwtProperties.getSecretKey()).thenReturn("aaaaa");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(new CustomUserDetail(normalUser));

        final String accessToken = jwtUtil.createAccessToken(normalUser);

        request.setCookies(
                createTokenCookie(REFRESH_TOKEN, null, jwtUtil.getRefreshTokenExpiredTime())
        );
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER + " " + accessToken);

        //when & then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(response.getCookie(REFRESH_TOKEN).getValue()).isNotBlank();

        verify(userDetailsService).loadUserByUsername(anyString());
        verify(jwtRefreshTokenService, never()).isEmptyRefreshToken(anyString());
        verify(jwtRefreshTokenService).updateOrSaveRefreshToken(anyString(), anyString());
        verify(jwtUtil, never()).verify(eq(""));
    }

    @Test
    @DisplayName("[요청 성공] AC 토큰 만료  RF 토큰이 유효한 경우 - AC 토큰 재발급 후 요청처리")
    public void givenExpiredACTokenRFToken_whenCheckFilter_thenACTokenReissueAndSuccess() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(jwtRefreshTokenService.isEmptyRefreshToken(anyString()))
                .thenReturn(false);
        when(jwtProperties.getAccessTokenExpiredTime())
                .thenReturn(Duration.ofSeconds(-1));
        when(jwtProperties.getRefreshTokenExpiredTime())
                .thenReturn(Duration.ofDays(7));
        when(jwtProperties.getSecretKey())
                .thenReturn("aaaaa");
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new CustomUserDetail(normalUser));

        final String accessToken = jwtUtil.createAccessToken(normalUser);
        final String refreshToken = jwtUtil.createRefreshToken(normalUser);

        requestSetToken(accessToken,refreshToken);

        //when & then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

        verify(userDetailsService).loadUserByUsername(anyString());
        verify(jwtRefreshTokenService).updateOrSaveRefreshToken(anyString(), anyString());
    }
    @Test
    @DisplayName("[요청 실패] AC 토큰 만료  RF 토큰이 만료한 경우 - 로그아웃 처리")
    public void givenExpiredACTokenAndRFToken_whenCheckFilter_thenLogout() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(jwtRefreshTokenService.isEmptyRefreshToken(anyString())).thenReturn(false);
        when(jwtProperties.getAccessTokenExpiredTime()).thenReturn(Duration.ofHours(-1));
        when(jwtProperties.getRefreshTokenExpiredTime()).thenReturn(Duration.ofSeconds(-1));
        when(jwtProperties.getSecretKey()).thenReturn("aaaaa");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(new CustomUserDetail(normalUser));

        final String accessToken = jwtUtil.createAccessToken(normalUser);
        final String refreshToken = jwtUtil.createRefreshToken(normalUser);

        requestSetToken(accessToken,refreshToken);

        //when & then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getCookie(ACCESS_TOKEN).getValue()).isBlank();
        assertThat(response.getCookie(REFRESH_TOKEN).getValue()).isBlank();

        verify(userDetailsService).loadUserByUsername(anyString());
        verify(jwtRefreshTokenService, never()).updateOrSaveRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("[요청 실패] AC 토큰 만료  RF 토큰이 존재하지 않을 경우 - 로그아웃 처리")
    public void givenExpiredACTokenNotFoundRFToken_whenCheckFilter_thenLogout() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(jwtRefreshTokenService.isEmptyRefreshToken(anyString())).thenReturn(true);
        when(jwtProperties.getAccessTokenExpiredTime()).thenReturn(Duration.ofHours(-1));
        when(jwtProperties.getRefreshTokenExpiredTime()).thenReturn(Duration.ofDays(7));
        when(jwtProperties.getSecretKey()).thenReturn("aaaaa");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(new CustomUserDetail(normalUser));

        final String accessToken = jwtUtil.createAccessToken(normalUser);

        request.setCookies(
                createTokenCookie(ACCESS_TOKEN, accessToken, jwtUtil.getAccessTokenExpiredTime())
        );
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER + " " + accessToken);

        //when & then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getCookie(ACCESS_TOKEN).getValue()).isBlank();
        assertThat(response.getCookie(REFRESH_TOKEN).getValue()).isBlank();

        verify(userDetailsService).loadUserByUsername(anyString());
        verify(jwtRefreshTokenService, never()).updateOrSaveRefreshToken(anyString(), anyString());
    }
}
