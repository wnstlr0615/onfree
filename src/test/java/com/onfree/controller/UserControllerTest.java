package com.onfree.controller;

import com.onfree.anotation.WithNormalUser;
import com.onfree.common.WebMvcBaseTest;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.core.dto.UpdateUserNotificationDto;
import com.onfree.core.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest extends WebMvcBaseTest {
    @MockBean
    UserService userService;

    @Test
    @WithNormalUser
    @DisplayName("[성공][PUT] 사용자 알림설정 변경")
    public void givenUserIdAndUpdateUserNotificationDto_whenUpdateUserNotification_thenSimpleResponseSuccess() throws Exception{
        //given
        final long userId = 1L;
        doNothing().when(userService)
                .updateUserNotification(eq(userId), any(UpdateUserNotificationDto.class));
        when(checker.isSelf(eq(1L)))
                .thenReturn(true);
        //when
        //then
        mvc.perform(put("/api/users/{userId}/notifications", userId)
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
    @WithNormalUser
    @DisplayName("[실패][PUT] 사용자 알림설정 변경 - 로그인하지 않은 다른 사람이 접근")
    public void givenOtherUserIdAndUpdateUserNotificationDto_whenUpdateUserNotification_thenAccessDeniedError() throws Exception{
        //given
        final long userId = 1L;
        final GlobalErrorCode errorCode = GlobalErrorCode.ACCESS_DENIED;
        when(checker.isSelf(eq(1L)))
                .thenThrow(new GlobalException(errorCode));
        //when
        //then
        mvc.perform(put("/api/users/{userId}/notifications", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateUserNotificationDto()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(userService, never()).updateUserNotification(eq(userId), any(UpdateUserNotificationDto.class));
    }

}