package com.onfree.core.service;

import com.onfree.core.dto.user.normal.NormalUserDetailDto;
import com.onfree.core.dto.user.normal.CreateNormalUserDto;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.UserRepository;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NormalUserServiceTest {
    @Mock
    UserRepository userRepository;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    NormalUserService normalUserService;

    @Test
    @DisplayName("[성공] 회원가입 요청 - 정상적인 요청 성공")
    public void givenCreateUserRes_whenCreateUser_thenReturnSuccessfulResponse() {
        //given
        final CreateNormalUserDto.Request userReq = givenCreateNormalUserReq();
        when(userRepository.save(any()))
                .thenReturn(
                        getNormalUserEntity(userReq)
                );
        when(userRepository.countByEmail(any()))
                .thenReturn(0);
        //when
        CreateNormalUserDto.Response response = normalUserService.addNormalUser(
                userReq
        );
        //then

        verify(userRepository, times(1)).save(any());
        assertThat(response)
                .hasFieldOrPropertyWithValue("adultCertification", userReq.getAdultCertification())
                .hasFieldOrPropertyWithValue("email", userReq.getEmail())
                .hasFieldOrPropertyWithValue("gender", userReq.getGender().getName())
                .hasFieldOrPropertyWithValue("name", userReq.getName())
                .hasFieldOrPropertyWithValue("nickname", userReq.getNickname())
                .hasFieldOrPropertyWithValue("newsAgency", userReq.getNewsAgency())
                .hasFieldOrPropertyWithValue("phoneNumber", userReq.getPhoneNumber())
                .hasFieldOrPropertyWithValue("bankName", userReq.getBankName().getBankName())
                .hasFieldOrPropertyWithValue("accountNumber", userReq.getAccountNumber())
                .hasFieldOrPropertyWithValue("advertisementAgree", userReq.getAdvertisementAgree())
                .hasFieldOrPropertyWithValue("personalInfoAgree", userReq.getPersonalInfoAgree())
                .hasFieldOrPropertyWithValue("policyAgree", userReq.getPolicyAgree())
                .hasFieldOrPropertyWithValue("serviceAgree", userReq.getServiceAgree())
                .hasFieldOrPropertyWithValue("profileImage", userReq.getProfileImage());
    }

    @Test
    @DisplayName("[실패] 회원가입 요청 - 이메일(아이디) 중복으로 인한 회원가입 실패")
    public void givenDuplicatedUserEmail_whenCreateUser_thenUserEmailDuplicatedError() {
        //given
        when(userRepository.countByEmail(any()))
                .thenReturn(1);
        UserErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;

        //when
        UserException userException = assertThrows(UserException.class,
                () -> normalUserService.addNormalUser(
                        givenCreateNormalUserReq()
                ));

        //then
        verify(userRepository, never()).save(any());
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    private CreateNormalUserDto.Request givenCreateNormalUserReq() {
        return CreateNormalUserDto.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .nickname("온프리프리")
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
    private NormalUser getNormalUserEntity(CreateNormalUserDto.Request request){
        return  getNormalUserEntity(request, 1L);
    }


    @Test
    @DisplayName("[성공] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenUserInfo() {
        //given
        final long userId = 1L;
        //when
        NormalUser normalUserEntity = getNormalUserEntity(userId);
        final NormalUserDetailDto userInfo = normalUserService.getUserDetail(normalUserEntity);
        //then
        assertThat(userInfo)
                .hasFieldOrPropertyWithValue("adultCertification", true)
                .hasFieldOrPropertyWithValue("email", "jun@naver.com")
                .hasFieldOrPropertyWithValue("gender", Gender.MAN.getName())
                .hasFieldOrPropertyWithValue("name", "준식")
                .hasFieldOrPropertyWithValue("nickname", "joon")
                .hasFieldOrPropertyWithValue("newsAgency", "SKT")
                .hasFieldOrPropertyWithValue("phoneNumber", "010-8888-9999")
                .hasFieldOrPropertyWithValue("bankName", "IBK기업은행")
                .hasFieldOrPropertyWithValue("accountNumber", "010-8888-9999")
                .hasFieldOrPropertyWithValue("advertisementAgree", true)
                .hasFieldOrPropertyWithValue("personalInfoAgree", true)
                .hasFieldOrPropertyWithValue("policyAgree", true)
                .hasFieldOrPropertyWithValue("serviceAgree", true)
                .hasFieldOrPropertyWithValue("profileImage", "http://onfree.io/images/123456789")
            ;
    }

    @Test
    @DisplayName("[성공] 사용자 계정 삭제")
    public void givenDeletedUserId_whenDeletedUser_thenDeleteUserResponse(){
        //given
        final long deletedUserId = 1L;
        when(userRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.of(getNormalUserEntity(
                                givenCreateNormalUserReq(), deletedUserId
                        ))
                );
        //when
        normalUserService.removeNormalUser(deletedUserId);
        //then

        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }

    private NormalUser getNormalUserEntity(CreateNormalUserDto.Request request, Long userId) {
        return getNormalUserEntity(request, userId, false);
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 - userId가 없는 경우")
    public void givenWrongDeletedUserId_whenDeletedUser_thenNotFoundUserId(){
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;

        when(userRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> normalUserService.removeNormalUser(deletedUserId));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 - 이미 삭제된 계정인 경우")
    public void givenAlreadyDeletedUserId_whenDeletedUser_thenAlreadyUserDeleted(){
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.ALREADY_USER_DELETED;
        final boolean deleted = true;

        when(userRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.of(
                                getNormalUserEntity(
                                        givenCreateNormalUserReq(), deletedUserId, deleted
                                )
                        )
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> normalUserService.removeNormalUser(deletedUserId));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }
    private NormalUser getNormalUserEntity(CreateNormalUserDto.Request request, Long userId, boolean deleted){
        final BankInfo bankInfo = getBankInfo(request.getBankName(), request.getAccountNumber());
        UserAgree userAgree = UserAgree.builder()
                .advertisement(request.getAdvertisementAgree())
                .personalInfo(request.getPersonalInfoAgree())
                .service(request.getServiceAgree())
                .policy(request.getPolicyAgree())
                .build();
        return NormalUser.builder()
                .userId(userId)
                .nickname(request.getNickname())
                .adultCertification(Boolean.TRUE)
                .email(request.getEmail())
                .password(request.getPassword())
                .gender(request.getGender())
                .name(request.getName())
                .newsAgency(request.getNewsAgency())
                .phoneNumber(request.getPhoneNumber())
                .bankInfo(bankInfo)
                .userAgree(userAgree)
                .adultCertification(request.getAdultCertification())
                .profileImage(request.getProfileImage())
                .deleted(deleted)
                .role(Role.NORMAL)
                .build();
    }
    @Test
    @DisplayName("[성공] 사용자 계정 수정 ")
    public void givenUpdateNormalUserReq_whenModifiedUser_thenReturnUpdateNormalUserResponse(){
        //given
        final long userId = 1L;
        final UpdateNormalUserDto.Request request = givenUpdateNormalUserReq();
        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.of(getNormalUserEntity(userId))
                );
        //when
        normalUserService.modifyNormalUser(userId, request);
        //then

        verify(userRepository, times(1)).findById(eq(userId));
    }
    public NormalUser getNormalUserEntity(long userId){
        return NormalUser.builder()
                .userId(userId)
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .nickname("joon")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .bankInfo(
                        getBankInfo(BankName.IBK_BANK, "010-8888-9999")
                )
                .userAgree(
                        getUserAgree()
                )
                .profileImage("http://onfree.io/images/123456789")
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
    private UpdateNormalUserDto.Request givenUpdateNormalUserReq() {
        return UpdateNormalUserDto.Request.builder()
                .nickname("온프리프리")
                .bankName(BankName.IBK_BANK)
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .build();
    }

    private BankInfo getBankInfo(BankName bankName, String accountNumber) {
        return BankInfo.builder()
                .bankName(bankName)
                .accountNumber(accountNumber)
                .build();
    }

}