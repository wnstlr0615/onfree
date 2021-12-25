package com.onfree.utils;

import com.onfree.config.error.code.LoginErrorCode;
import com.onfree.config.error.exception.LoginException;
import com.onfree.core.entity.user.*;
import com.onfree.core.model.VerifyResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class JWTUtilTest {

    @Test
    @DisplayName("[성공] 토큰 생성 후 검증 통과")
    public void givenSuccessToken_whenVerify_thenSuccessVerify() throws Exception{
        //given
        final NormalUser user = givenNormalUser();
        final String token = JWTUtil.createToken(user);

        //when
        final VerifyResult verify = JWTUtil.verify(token);
        //then
        assertThat(verify)
                .hasFieldOrPropertyWithValue("result", true)
                .hasFieldOrPropertyWithValue("username", "jun@naver.com")
            ;
    }

    private NormalUser givenNormalUser() {
        final BankInfo bankInfo = getBankInfo(BankName.IBK_BANK, "010-8888-9999");
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
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .newsAgency("SKT")
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
    public void givenExpireToken_whenVerify_thenFailVerify() throws Exception{

        //given
        final LocalDateTime mock = Mockito.mock(LocalDateTime.class);
        when(mock.plusMinutes(any(Long.class)))
                .thenReturn(LocalDateTime.now().minusSeconds(1));

        mockStatic(LocalDateTime.class)
                .when(LocalDateTime::now).thenReturn(mock);

        final NormalUser user = givenNormalUser();
        final String token = JWTUtil.createToken(user);

        //when
        final LoginException loginException = assertThrows(LoginException.class,
                () -> JWTUtil.verify(token));
        // then
        assertThat(loginException)
                .hasFieldOrPropertyWithValue("errorCode", LoginErrorCode.TOKEN_IS_EXPIRED);
    }

    @Test
    @DisplayName("[실패] 토큰 생성 시 null 이 입력된 경우 ")
    @Disabled("@NonNull로 null 방지 처리")
    public void givenNull_whenCreateToken_thenFailVerify() throws Exception{
        //given
        final String token = JWTUtil.createToken(null);

        //when
        final VerifyResult verify = JWTUtil.verify(token);

        //then
        assertThat(verify).isNull();
        
    }
}