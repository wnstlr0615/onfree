package com.onfree.utils;

import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.user.*;
import com.onfree.common.model.VerifyResult;
import com.onfree.common.properties.JWTProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
class JWTUtilTest {
    public static final Duration HALF_HOUR = Duration.ofMinutes(30);
    public static final Duration ONE_WEEK = Duration.ofDays(7);
    JWTUtil jwtUtil;
    JWTProperties jwtProperties;
    @BeforeEach
    public void setUp(){
        jwtProperties=new JWTProperties(HALF_HOUR, ONE_WEEK,"abc");
        jwtUtil=new JWTUtil(jwtProperties);
    }
    @Test
    @DisplayName("[성공] access 토큰 생성 후 검증 통과")
    public void givenSuccessToken_whenVerify_thenSuccessVerify(){
        //given
        final NormalUser user = givenNormalUser("jun@naver.com");
        final String token = jwtUtil.createAccessToken(user);

        //when
        final VerifyResult verify = jwtUtil.verify(token);

        //then
        assertThat(verify)
                .hasFieldOrPropertyWithValue("result", true)
                .hasFieldOrPropertyWithValue("username", "jun@naver.com")
            ;
    }

    private NormalUser givenNormalUser(String email) {
        final BankInfo bankInfo = getBankInfo(BankName.IBK, "010-8888-9999");
        UserAgree userAgree = UserAgree.builder()
                .advertisement(true)
                .personalInfo(true)
                .service(true)
                .policy(true)
                .build();

        return NormalUser.builder()
                .userId(1L)
                .nickname("온프리프리")
                .adultCertification(Boolean.TRUE)
                .email(email)
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-8888-9999")
                .bankInfo(bankInfo)
                .userAgree(userAgree)
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/123456789")
                .deleted(false)
                .role(Role.NORMAL)
                .build();
    }
    private BankInfo getBankInfo(BankName bankName, String accountNumber) {
        return BankInfo.builder()
                .bankName(bankName)
                .accountNumber(accountNumber)
                .build();
    }

    @Test
    @DisplayName("[실패] 토큰 생성 후 시간이 만료된 경우 ")
    public void givenExpireToken_whenVerify_thenFailVerify(){
        //given
      /*  staticMock 다른 메소드에 영향을 줘서 임시로 다음과 같이 수정
      final LocalDateTime mock = Mockito.mock(LocalDateTime.class);
        when(mock.plusMinutes(any(Long.class)))
                .thenReturn(LocalDateTime.now().minusSeconds(1));
        mockStatic(LocalDateTime.class).when(LocalDateTime::now).thenReturn(mock);
        */

        
        final NormalUser user = givenNormalUser("jun123@naver.com");
        final String token = jwtUtil.createAccessToken(user, -1L);

        //when
        final VerifyResult verify = jwtUtil.verify(token);

        // then
        assertThat(verify.isResult()).isFalse();
    }


    @Test
    @DisplayName("[성공] refresh 토큰 생성 후 검증 통과")
    public void givenSuccessRefreshToken_whenVerify_thenSuccessVerify(){
        //given
        final NormalUser user = givenNormalUser("jun@naver.com");
        final String token = jwtUtil.createRefreshToken(user);

        //when
        final VerifyResult verify = jwtUtil.verify(token);

        //then
        assertThat(verify.isResult()).isTrue();
        assertThat(verify.getUsername()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("[실패] refresh 토큰 생성 후 시간이 만료된 경우 ")
    public void givenExpireRefreshToken_whenVerify_thenLoginException(){
        //given
        final NormalUser user = givenNormalUser("jun123@naver.com");
        final String token = jwtUtil.createRefreshToken(user, -1L);

        //when
        final VerifyResult verify = jwtUtil.verify(token);

        //then
        assertThat(verify.isResult()).isFalse();
    }
}