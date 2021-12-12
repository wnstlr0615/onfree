package com.onfree.core.service;

import com.onfree.core.dto.NormalUserInfo;
import com.onfree.core.dto.user.CreateNormalUser;
import com.onfree.core.entity.user.BankName;
import com.onfree.core.entity.user.Gender;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.repository.UserRepository;
import com.onfree.error.code.UserErrorCode;
import com.onfree.error.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("[성공] 회원가입 요청 - 정상적인 요청 성공")
    public void givenCreateUserRes_whenCreateUser_thenReturnSuccessfulResponse() throws Exception {
        //given
        when(userRepository.save(any()))
                .thenReturn(
                        getNormalUserEntity(givenCreateNormalUserReq())
                );
        when(userRepository.countByEmail(any()))
                .thenReturn(0);
        //when
        CreateNormalUser.Response response = userService.createNormalUser(
                givenCreateNormalUserReq()
        );
        //then
        verify(userRepository, times(1)).save(any());
        assertThat(response)
                .hasFieldOrPropertyWithValue("adultCertification", Boolean.TRUE)
                .hasFieldOrPropertyWithValue("email", "jun@naver.com")
                .hasFieldOrPropertyWithValue("gender", Gender.MAN.getName())
                .hasFieldOrPropertyWithValue("name", "준식")
                .hasFieldOrPropertyWithValue("newsAgency", "SKT")
                .hasFieldOrPropertyWithValue("phoneNumber", "010-8888-9999")
                .hasFieldOrPropertyWithValue("bankName", BankName.IBK_BANK.getBankName())
                .hasFieldOrPropertyWithValue("accountNumber", "010-8888-9999")
                .hasFieldOrPropertyWithValue("advertisementAgree", true)
                .hasFieldOrPropertyWithValue("personalInfoAgree", true)
                .hasFieldOrPropertyWithValue("policyAgree", true)
                .hasFieldOrPropertyWithValue("serviceAgree", true)
                .hasFieldOrPropertyWithValue("profileImage", "http://onfree.io/images/123456789");
    }

    @Test
    @DisplayName("[실패] 회원가입 요청 - 이메일(아이디) 중복으로 인한 회원가입 실패")
    public void givenDuplicatedUserEmail_whenCreateUser_thenUserEmailDuplicatedError() throws Exception {
        //given
        when(userRepository.countByEmail(any()))
                .thenReturn(1);
        UserErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;

        //when
        UserException userException = assertThrows(UserException.class,
                () -> userService.createNormalUser(
                        givenCreateNormalUserReq()
                ));

        //then
        verify(userRepository, never()).save(any());
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    private CreateNormalUser.Request givenCreateNormalUserReq() {
        return CreateNormalUser.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .bankName(BankName.IBK_BANK)
                .accountNumber("010-8888-9999")
                .advertisementAgree(true)
                .personalInfoAgree(true)
                .policyAgree(true)
                .serviceAgree(true)
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }

    private NormalUser getNormalUserEntity(CreateNormalUser.Request request) {
        return request.toEntity();
    }

    @Test
    @DisplayName("[성공] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenUserInfo() throws Exception {
        //given
        final long userId = 1L;
        final CreateNormalUser.Request request = givenCreateNormalUserReq();
        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.of(getNormalUserEntity(request))
                );
        //when
        final NormalUserInfo userInfo = userService.getUserInfo(userId);
        //then
        verify(userRepository, times(1)).findById(any());
        assertThat(userInfo)
                .hasFieldOrPropertyWithValue("adultCertification", request.getAdultCertification())
                .hasFieldOrPropertyWithValue("email", request.getEmail())
                .hasFieldOrPropertyWithValue("gender", request.getGender().getName())
                .hasFieldOrPropertyWithValue("name", request.getName())
                .hasFieldOrPropertyWithValue("newsAgency", request.getNewsAgency())
                .hasFieldOrPropertyWithValue("phoneNumber", request.getPhoneNumber())
                .hasFieldOrPropertyWithValue("bankName", request.getBankName().getBankName())
                .hasFieldOrPropertyWithValue("accountNumber", request.getAccountNumber())
                .hasFieldOrPropertyWithValue("advertisementAgree", request.getAdvertisementAgree())
                .hasFieldOrPropertyWithValue("personalInfoAgree", request.getPersonalInfoAgree())
                .hasFieldOrPropertyWithValue("policyAgree", request.getPolicyAgree())
                .hasFieldOrPropertyWithValue("serviceAgree", request.getServiceAgree())
                .hasFieldOrPropertyWithValue("profileImage", request.getProfileImage());
    }

    @Test
    @DisplayName("[실패] 사용자 정보 조회 - 없는 유저 아이디로 조회")
    public void givenWrongUserId_whenGetUserInfo_thenNotFoundUserId() throws Exception {
        //given
        final long userId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;

        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> userService.getUserInfo(userId)
        );

        //then
        verify(userRepository, times(1)).findById(any());
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
    }
}