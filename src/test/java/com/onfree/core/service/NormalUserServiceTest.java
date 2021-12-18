package com.onfree.core.service;

import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.NormalUserInfo;
import com.onfree.core.dto.user.CreateNormalUser;
import com.onfree.core.dto.user.UpdateNormalUser;
import com.onfree.core.entity.user.*;
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
class NormalUserServiceTest {
    @Mock
    UserRepository userRepository;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    NormalUserService normalUserService;

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
        CreateNormalUser.Response response = normalUserService.createNormalUser(
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
                () -> normalUserService.createNormalUser(
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
    private NormalUser getNormalUserEntity(CreateNormalUser.Request request){
        return  getNormalUserEntity(request, 1L);
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
        final NormalUserInfo userInfo = normalUserService.getUserInfo(userId);
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
                () -> normalUserService.getUserInfo(userId)
        );

        //then
        verify(userRepository, times(1)).findById(any());
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
    }
    @Test
    @DisplayName("[성공] 사용자 계정 삭제")
    public void givenDeletedUserId_whenDeletedUser_thenDeleteUserResponse() throws Exception{
        //given
        final long deletedUserId = 1L;
        when(userRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.of(getNormalUserEntity(
                                givenCreateNormalUserReq(), deletedUserId
                        ))
                );
        //when
        final DeletedUserResponse deletedUserResponse = normalUserService.deletedNormalUser(deletedUserId);
        //then
        assertThat(deletedUserResponse)
                .hasFieldOrPropertyWithValue("userId", deletedUserId)
                .hasFieldOrPropertyWithValue("deleted", true);
        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }

    private NormalUser getNormalUserEntity(CreateNormalUser.Request request, Long userId) {
        return getNormalUserEntity(request, userId, false);
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 - userId가 없는 경우")
    public void givenWrongDeletedUserId_whenDeletedUser_thenNotFoundUserId() throws Exception{
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;

        when(userRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> normalUserService.deletedNormalUser(deletedUserId));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 - 이미 삭제된 계정인 경우")
    public void givenAlreadyDeletedUserId_whenDeletedUser_thenAlreadyUserDeleted() throws Exception{
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
                () -> normalUserService.deletedNormalUser(deletedUserId));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }
    private NormalUser getNormalUserEntity(CreateNormalUser.Request request, Long userId, boolean deleted){
        final BankInfo bankInfo = getBankInfo(request.getBankName(), request.getAccountNumber());
        UserAgree userAgree = UserAgree.builder()
                .advertisement(request.getAdvertisementAgree())
                .personalInfo(request.getPersonalInfoAgree())
                .service(request.getServiceAgree())
                .policy(request.getPolicyAgree())
                .build();
        return NormalUser.builder()
                .userId(userId)
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
    public void givenUpdateNormalUserReq_whenModifiedUser_thenReturnUpdateNormalUserResponse() throws Exception{
        //given
        final long userId = 1L;
        final UpdateNormalUser.Request request = givenUpdateNormalUserReq();
        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.of(getNormalUserEntity(userId))
                );
        //when
        final UpdateNormalUser.Response response = normalUserService.modifyedUser(userId, request);
        //then
        assertThat(response)
            .hasFieldOrPropertyWithValue("nickname",request.getNickname())
            .hasFieldOrPropertyWithValue("bankName",request.getBankName())
            .hasFieldOrPropertyWithValue("accountNumber",request.getAccountNumber())
            .hasFieldOrPropertyWithValue("newsAgency",request.getNewsAgency())
            .hasFieldOrPropertyWithValue("phoneNumber",request.getPhoneNumber())
            .hasFieldOrPropertyWithValue("adultCertification",request.getAdultCertification())
            .hasFieldOrPropertyWithValue("profileImage",request.getProfileImage())
        ;
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
    private UpdateNormalUser.Request givenUpdateNormalUserReq() {
        return UpdateNormalUser.Request.builder()
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
    @Test
    @DisplayName("[실패] 사용자 계정 수정 - userId가 존재하지 않는 경우")
    public void givenWrongUserId_whenModifiedUser_thenNorFoundUserId() throws Exception{
        //given
        final long wrongUserId = 1L;
        final UpdateNormalUser.Request request = givenUpdateNormalUserReq();
        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class, () -> normalUserService.modifyedUser(wrongUserId, request));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_FOUND_USERID)
                .hasFieldOrPropertyWithValue("errorMessage", UserErrorCode.NOT_FOUND_USERID.getDescription())
        ;
        verify(userRepository, times(1)).findById(eq(wrongUserId));
    }
}