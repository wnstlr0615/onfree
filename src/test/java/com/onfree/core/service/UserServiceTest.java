package com.onfree.core.service;


import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.dto.UpdateUserNotificationDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("[성공] 사용자 알림 설정 ")
    public void givenUserIdAndUpdateUserNotificationDto_whenUpdateUserNotification_thenUpdateUser() throws Exception{
        //given
        final long givenUserId = 1L;
        final NormalUser givenUser = getUser();
        when(userRepository.findById(anyLong()))
                .thenReturn(
                        Optional.ofNullable(
                                givenUser
                        )
                );
        //when //then
        assertThat(givenUser.getUserNotification())
                .hasFieldOrPropertyWithValue("pushRequestNotification", true).as("PUSH 알림 동의")
                .hasFieldOrPropertyWithValue("kakaoRequestNotification", true).as("카카오 의뢰 요청 알림 동의")
                .hasFieldOrPropertyWithValue("kakaoNewsNotification", true).as("카카오 최신 뉴스 알림 동의")
                .hasFieldOrPropertyWithValue("emailRequestNotification", true).as("이메일 의뢰 요청 알림 동의")
                .hasFieldOrPropertyWithValue("emailNewsNotification", true).as("이메일 최신 뉴스 알림 동의")
                ;

        userService.updateUserNotification(givenUserId, givenUpdateUserNotificationDto());

        assertThat(givenUser.getUserNotification())
                .hasFieldOrPropertyWithValue("pushRequestNotification", true).as("PUSH 알림 동의")
                .hasFieldOrPropertyWithValue("kakaoRequestNotification", true).as("카카오 의뢰 요청 알림 동의")
                .hasFieldOrPropertyWithValue("kakaoNewsNotification", false).as("카카오 최신 뉴스 알림 동의")
                .hasFieldOrPropertyWithValue("emailRequestNotification", false).as("이메일 의뢰 요청 알림 동의")
                .hasFieldOrPropertyWithValue("emailNewsNotification", false).as("이메일 최신 뉴스 알림 동의")
        ;
        verify(userRepository).findById(eq(givenUserId));
    }

    private UpdateUserNotificationDto givenUpdateUserNotificationDto() {
        return UpdateUserNotificationDto.builder()
                .pushRequestNotification(true)
                .kakaoRequestNotification(true)
                .kakaoNewsNotification(false)
                .emailRequestNotification(false)
                .emailNewsNotification(false)
                .build();
    }

    private NormalUser getUser() {

        return NormalUser.builder()
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .nickname("온프리프리")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .bankInfo(
                        getBankInfo(BankName.IBK_BANK, "010-0000-0000")
                )
                .userAgree(
                        getUserAgree()
                )
                .role(Role.NORMAL)
                .build();
    }

    private UserAgree getUserAgree() {
        return UserAgree.builder()
                .advertisement(true)
                .policy(true)
                .service(true)
                .personalInfo(true)
                .build();
    }
    private BankInfo getBankInfo(BankName bankName, String accountNumber) {
        return BankInfo.builder()
                .bankName(bankName)
                .accountNumber(accountNumber)
                .build();
    }

    @Test
    @DisplayName("[실패] 사용자 알림 설정 - 사용자가 없는 경우")
    public void givenWrongUserId_whenUpdateUserNotification_thenNotFoundUserIdError() throws Exception{
        //given
        final long givenUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(userRepository.findById(anyLong()))
                .thenReturn(
                        Optional.empty()
                );
        //when //then
        final UserException userException = assertThrows(UserException.class,
                () -> userService.updateUserNotification(givenUserId, givenUpdateUserNotificationDto())
        );
        assertThat(userException.getErrorCode()).isEqualTo(errorCode);
        assertThat(userException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription());

        verify(userRepository).findById(eq(givenUserId));
    }
}