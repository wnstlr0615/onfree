package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.config.webmvc.resolver.LoginUserArgumentResolver;
import com.onfree.core.dto.user.UpdateUserNotificationDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest extends ControllerBaseTest {
    @MockBean
    UserService userService;
    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @Test
    @WithNormalUser
    @DisplayName("[성공][PUT] 일반 사용자 알림설정 변경")
    public void givenNormalUserIdAndUpdateUserNotificationDto_whenUserNotificationModify_thenSimpleResponseSuccess() throws Exception{
        //given
        final long userId = 1L;
        doNothing().when(userService)
                .updateUserNotification(anyLong(), any(UpdateUserNotificationDto.class));
        when(loginUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getNormalUser()
                );
        //when //then
        mvc.perform(put("/api/v1/users/me/notifications", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateUserNotificationDto()
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("알림설정이 변경되었습니다."))
        ;
        verify(userService).updateUserNotification(eq(userId), any(UpdateUserNotificationDto.class));
    }

    public NormalUser getNormalUser(){
        return NormalUser.builder()
                .userId(1L)
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .bankInfo(
                        BankInfo.createBankInfo(BankName.IBK_BANK, "010-8888-9999")
                )
                .userAgree(
                        UserAgree.createUserAgree(true,true,true,true)
                )
                .profileImage("http://onfree.io/images/123456789")
                .build();

    }

    private UpdateUserNotificationDto givenUpdateUserNotificationDto() {
        return UpdateUserNotificationDto.builder()
                .emailNewsNotification(true)
                .emailRequestNotification(true)
                .kakaoNewsNotification(false)
                .kakaoRequestNotification(false)
                .pushRequestNotification(true)
                .build();
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 작가 사용자 알림설정 변경")
    public void givenArtistUserIdAndUpdateUserNotificationDto_whenUserNotificationModify_thenSimpleResponseSuccess() throws Exception{
        //given
        final long userId = 1L;
        doNothing().when(userService)
                .updateUserNotification(eq(userId), any(UpdateUserNotificationDto.class));
        when(loginUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(getArtistUser());
        //when //then
        mvc.perform(put("/api/v1/users/me/notifications", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateUserNotificationDto()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("알림설정이 변경되었습니다."))
        ;
        verify(userService).updateUserNotification(eq(userId), any(UpdateUserNotificationDto.class));
    }

    private ArtistUser getArtistUser() {
        return ArtistUser.builder()
                .userId(1L)
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon@naver.com")
                .password("{bcrypt}onfree")
                .gender(Gender.MAN)
                .name("joon")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .bankInfo(BankInfo.createBankInfo(BankName.IBK_BANK, "010-0000-0000"))
                .userAgree(UserAgree.createUserAgree(true, true, true, true))
                .adultCertification(true)
                .profileImage("http://www.onfree.co.kr/images/dasdasfasd")
                .deleted(false)
                .role(Role.ARTIST)
                .portfolioUrl("http://www.onfree.co.kr/folioUrl/dasdasfasd")
                .build();
    }

}