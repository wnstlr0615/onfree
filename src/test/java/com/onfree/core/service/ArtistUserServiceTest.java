package com.onfree.core.service;

import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.artist.ArtistUserDetail;
import com.onfree.core.dto.user.artist.CreateArtistUser;
import com.onfree.core.dto.user.artist.UpdateArtistUser;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.UserRepository;
import com.onfree.error.code.UserErrorCode;
import com.onfree.error.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ArtistUserServiceTest {
    @Mock
    UserRepository userRepository;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    ArtistUserService artistUserService;

    @Test
    @DisplayName("[성공] 회원가입 요청 - 정상적인 요청 성공")
    public void givenCreateUserRes_whenCreateUser_thenReturnSuccessfulResponse() throws Exception {
        //given
        final CreateArtistUser.Request userReq = givenCreateArtistUserReq();
        when(userRepository.save(any()))
                .thenReturn(
                        getArtistUserEntity(userReq)
                );
        when(userRepository.countByEmail(any()))
                .thenReturn(0);
        //when
        CreateArtistUser.Response response = artistUserService.createArtistUser(
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
                .hasFieldOrPropertyWithValue("profileImage", userReq.getProfileImage())
                .hasFieldOrPropertyWithValue("portfolioUrl", userReq.getPortfolioUrl())
        ;
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
                () -> artistUserService.createArtistUser(
                        givenCreateArtistUserReq()
                ));

        //then
        verify(userRepository, never()).save(any());
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    private CreateArtistUser.Request givenCreateArtistUserReq() {
        return CreateArtistUser.Request
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
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .build();
    }
    private ArtistUser getArtistUserEntity(CreateArtistUser.Request request){
        return  getArtistUserEntity(request, 1L);
    }


    @Test
    @DisplayName("[성공] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenUserInfo() throws Exception {
        //given
        final long userId = 1L;
        final CreateArtistUser.Request request = givenCreateArtistUserReq();
        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.of(
                                getArtistUserEntity(request)
                        )
                );
        //when
        final ArtistUserDetail userInfo = artistUserService.getUserDetail(userId);
        //then
        verify(userRepository, times(1)).findById(any());
        assertThat(userInfo)
                .hasFieldOrPropertyWithValue("adultCertification", request.getAdultCertification())
                .hasFieldOrPropertyWithValue("email", request.getEmail())
                .hasFieldOrPropertyWithValue("gender", request.getGender().getName())
                .hasFieldOrPropertyWithValue("name", request.getName())
                .hasFieldOrPropertyWithValue("nickname", request.getNickname())
                .hasFieldOrPropertyWithValue("newsAgency", request.getNewsAgency())
                .hasFieldOrPropertyWithValue("phoneNumber", request.getPhoneNumber())
                .hasFieldOrPropertyWithValue("bankName", request.getBankName().getBankName())
                .hasFieldOrPropertyWithValue("accountNumber", request.getAccountNumber())
                .hasFieldOrPropertyWithValue("advertisementAgree", request.getAdvertisementAgree())
                .hasFieldOrPropertyWithValue("personalInfoAgree", request.getPersonalInfoAgree())
                .hasFieldOrPropertyWithValue("policyAgree", request.getPolicyAgree())
                .hasFieldOrPropertyWithValue("serviceAgree", request.getServiceAgree())
                .hasFieldOrPropertyWithValue("profileImage", request.getProfileImage())
                .hasFieldOrPropertyWithValue("portfolioUrl", request.getPortfolioUrl())
        ;
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
                () -> artistUserService.getUserDetail(userId)
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
                        Optional.of(getArtistUserEntity(
                                givenCreateArtistUserReq(), deletedUserId
                        ))
                );
        //when
        final DeletedUserResponse deletedUserResponse = artistUserService.deletedArtistUser(deletedUserId);
        //then
        assertThat(deletedUserResponse)
                .hasFieldOrPropertyWithValue("userId", deletedUserId)
                .hasFieldOrPropertyWithValue("deleted", true);
        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }

    private ArtistUser getArtistUserEntity(CreateArtistUser.Request request, Long userId) {
        return getArtistUserEntity(request, userId, false);
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
                () -> artistUserService.deletedArtistUser(deletedUserId));
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
                                getArtistUserEntity(
                                        givenCreateArtistUserReq(), deletedUserId, deleted
                                )
                        )
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> artistUserService.deletedArtistUser(deletedUserId));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
        verify(userRepository, times(1)).findById(eq(deletedUserId));
    }
    private ArtistUser getArtistUserEntity(CreateArtistUser.Request request, Long userId, boolean deleted){
        final BankInfo bankInfo = getBankInfo(request.getBankName(), request.getAccountNumber());
        UserAgree userAgree = UserAgree.builder()
                .advertisement(request.getAdvertisementAgree())
                .personalInfo(request.getPersonalInfoAgree())
                .service(request.getServiceAgree())
                .policy(request.getPolicyAgree())
                .build();
        return ArtistUser.builder()
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
                .portfolioUrl(request.getPortfolioUrl())
                .deleted(deleted)
                .role(Role.ARTIST)
                .build();
    }
    @Test
    @DisplayName("[성공] 사용자 계정 수정 ")
    public void givenUpdateArtistUserReq_whenModifiedUser_thenReturnUpdateArtistUserResponse() throws Exception{
        //given
        final long userId = 1L;
        final UpdateArtistUser.Request request = givenUpdateArtistUserReq();
        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.of(getArtistUserEntity(userId))
                );
        //when
        final UpdateArtistUser.Response response = artistUserService.modifiedUser(userId, request);
        //then
        assertThat(response)
            .hasFieldOrPropertyWithValue("nickname",request.getNickname())
            .hasFieldOrPropertyWithValue("bankName",request.getBankName())
            .hasFieldOrPropertyWithValue("accountNumber",request.getAccountNumber())
            .hasFieldOrPropertyWithValue("newsAgency",request.getNewsAgency())
            .hasFieldOrPropertyWithValue("phoneNumber",request.getPhoneNumber())
            .hasFieldOrPropertyWithValue("adultCertification",request.getAdultCertification())
            .hasFieldOrPropertyWithValue("profileImage",request.getProfileImage())
            .hasFieldOrPropertyWithValue("portfolioUrl",request.getPortfolioUrl())
        ;
        verify(userRepository, times(1)).findById(eq(userId));
    }
    public ArtistUser getArtistUserEntity(long userId){
        return ArtistUser.builder()
                .userId(userId)
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
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
    private UpdateArtistUser.Request givenUpdateArtistUserReq() {
        return UpdateArtistUser.Request.builder()
                .nickname("온프리프리")
                .bankName(BankName.IBK_BANK)
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
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
        final UpdateArtistUser.Request request = givenUpdateArtistUserReq();
        when(userRepository.findById(any()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class, () -> artistUserService.modifiedUser(wrongUserId, request));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_FOUND_USERID)
                .hasFieldOrPropertyWithValue("errorMessage", UserErrorCode.NOT_FOUND_USERID.getDescription())
        ;
        verify(userRepository, times(1)).findById(eq(wrongUserId));
    }
}