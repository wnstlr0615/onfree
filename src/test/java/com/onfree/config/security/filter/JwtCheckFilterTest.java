package com.onfree.config.security.filter;

import com.onfree.common.properties.JWTProperties;
import com.onfree.config.security.CustomUserDetail;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.user.Gender;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.entity.user.Role;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.user.LoginService;
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
    @SpyBean
    JwtCheckFilter jwtCheckFilter;
    @MockBean
    CustomUserDetailService userDetailsService;
    @MockBean
    LoginService loginService;
    
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
    @DisplayName("[?????? ??????] AC ????????? ???????????? RF ????????? ????????? ??????")
    public void givenACAndRFToken_whenCheckFilter_thenSuccess() throws Exception {
        //given
        final User normalUser = createNormalUser();

        when(loginService.isWrongRefreshToken(anyString(), anyString())).thenReturn(false);
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
        verify(loginService).isWrongRefreshToken(anyString(), anyString());
        verify(loginService, never()).saveRefreshToken(anyString(), anyString());

    }

    private User createNormalUser() {
        return NormalUser.builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("{noop}!Abcderghijk112")
                .gender(Gender.MAN)
                .nickname("???????????????")
                .name("??????")
                .mobileCarrier(MobileCarrier.SKT)
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
    @DisplayName("[?????? ??????] AC ????????? ?????? ??????  RF ????????? ???????????? ?????? ?????? - RF ?????? ????????? ??? ????????????")
    public void givenACTokenNotValidRFToken_whenCheckFilter_thenRFTokenReissueAndSuccess() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(loginService.isWrongRefreshToken(anyString(), anyString())).thenReturn(false);
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
        verify(loginService).isWrongRefreshToken(anyString(), anyString());
        verify(loginService).saveRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("[?????? ??????] AC ????????? ?????? ??????  RF ????????? DB??? ?????? ?????? ?????? ?????? - ???????????? ?????? ?????? ?????? & ????????? ??????")
    public void givenACTokenNotFoundRFTokenInDB_whenCheckFilter_thenLogout() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(loginService.isWrongRefreshToken(anyString(), anyString())).thenReturn(true);
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
        verify(loginService).isWrongRefreshToken(anyString(), anyString());
        verify(loginService, never()).saveRefreshToken(anyString(), anyString());
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
    @DisplayName("[?????? ??????] AC ????????? ?????? ??????  RF ?????? ????????? ?????? ?????? ?????? ?????? - RF ?????? ????????? ??? ????????????")
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
        verify(loginService, never()).isWrongRefreshToken(anyString(), anyString());
        verify(loginService).saveRefreshToken(anyString(), anyString());
        verify(jwtUtil, never()).verify(eq(""));
    }

    @Test
    @DisplayName("[?????? ??????] AC ?????? ??????  RF ????????? ????????? ?????? - AC ?????? ????????? ??? ????????????")
    public void givenExpiredACTokenRFToken_whenCheckFilter_thenACTokenReissueAndSuccess() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(loginService.isWrongRefreshToken(anyString(), anyString()))
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
//        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        jwtCheckFilter.doFilterInternal(request, response, mockFilterChain);
//        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();


        verify(userDetailsService).loadUserByUsername(anyString());
        verify(loginService).saveRefreshToken(anyString(), anyString());
    }
    @Test
    @DisplayName("[?????? ??????] AC ?????? ??????  RF ????????? ????????? ?????? - ???????????? ??????")
    public void givenExpiredACTokenAndRFToken_whenCheckFilter_thenLogout() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(loginService.isWrongRefreshToken(anyString(), anyString())).thenReturn(false);
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
        verify(loginService, never()).saveRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("[?????? ??????] AC ?????? ??????  RF ????????? ???????????? ?????? ?????? - ???????????? ??????")
    public void givenExpiredACTokenNotFoundRFToken_whenCheckFilter_thenLogout() throws Exception {
        //given
        final User normalUser = createNormalUser();
        when(loginService.isWrongRefreshToken(anyString(), anyString())).thenReturn(true);
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
        verify(loginService, never()).saveRefreshToken(anyString(), anyString());
    }
}
