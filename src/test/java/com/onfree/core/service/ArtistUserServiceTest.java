package com.onfree.core.service;

import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.artist.ArtistUserDetailDto;
import com.onfree.core.dto.user.artist.CreateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.ArtistUserRepository;
import com.onfree.core.repository.UserRepository;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
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
    ArtistUserRepository artistUserRepository;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    ArtistUserService artistUserService;

    @Test
    @DisplayName("[성공] 회원가입 요청 - 정상적인 요청 성공")
    public void givenCreateUserRes_whenAddUser_thenReturnSuccessfulResponse() {
        //given
        final CreateArtistUserDto.Request userReq = givenCreateArtistUserReq();
        when(artistUserRepository.save(any()))
                .thenReturn(
                        getArtistUserEntity(userReq)
                );
        when(artistUserRepository.countByEmail(any()))
                .thenReturn(0);
        //when
        CreateArtistUserDto.Response response = artistUserService.addArtistUser(
                userReq
        );
        //then

        verify(artistUserRepository, times(1)).save(any());
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
    public void givenDuplicatedUserEmail_whenAddUser_thenUserEmailDuplicatedError() {
        //given
        when(artistUserRepository.countByEmail(any()))
                .thenReturn(1);
        UserErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;

        //when
        UserException userException = assertThrows(UserException.class,
                () -> artistUserService.addArtistUser(
                        givenCreateArtistUserReq()
                ));

        //then
        verify(artistUserRepository, never()).save(any());
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

    }

    private CreateArtistUserDto.Request givenCreateArtistUserReq() {
        return CreateArtistUserDto.Request
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
    private ArtistUser getArtistUserEntity(CreateArtistUserDto.Request request){
        return  getArtistUserEntity(request, 1L);
    }


    @Test
    @DisplayName("[성공] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenUserInfo() {
        //given
        final long userId = 1L;
        final CreateArtistUserDto.Request request = givenCreateArtistUserReq();
        when(artistUserRepository.findById(any()))
                .thenReturn(
                        Optional.of(
                                getArtistUserEntity(request)
                        )
                );
        //when
        final ArtistUserDetailDto userInfo = artistUserService.getUserDetail(userId);
        //then
        verify(artistUserRepository, times(1)).findById(any());
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
    public void givenWrongUserId_whenGetUserInfo_thenNotFoundUserId() {
        //given
        final long userId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;

        when(artistUserRepository.findById(any()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> artistUserService.getUserDetail(userId)
        );

        //then
        verify(artistUserRepository, times(1)).findById(any());
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
    }
    @Test
    @DisplayName("[성공] 사용자 계정 삭제")
    public void givenDeletedUserId_whenDeletedUser_thenDeleteUserResponse(){
        //given
        final long deletedUserId = 1L;
        when(artistUserRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.of(getArtistUserEntity(
                                givenCreateArtistUserReq(), deletedUserId
                        ))
                );
        //when
        artistUserService.removeArtistUser(deletedUserId);

        //then
        verify(artistUserRepository, times(1)).findById(eq(deletedUserId));
    }

    private ArtistUser getArtistUserEntity(CreateArtistUserDto.Request request, Long userId) {
        return getArtistUserEntity(request, userId, false);
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 - userId가 없는 경우")
    public void givenWrongDeletedUserId_whenDeletedUser_thenNotFoundUserId(){
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;

        when(artistUserRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> artistUserService.removeArtistUser(deletedUserId));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
        verify(artistUserRepository, times(1)).findById(eq(deletedUserId));
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 - 이미 삭제된 계정인 경우")
    public void givenAlreadyDeletedUserId_whenDeletedUser_thenAlreadyUserDeleted(){
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.ALREADY_USER_DELETED;
        final boolean deleted = true;

        when(artistUserRepository.findById(deletedUserId))
                .thenReturn(
                        Optional.of(
                                getArtistUserEntity(
                                        givenCreateArtistUserReq(), deletedUserId, deleted
                                )
                        )
                );
        //when
        final UserException userException = assertThrows(UserException.class,
                () -> artistUserService.removeArtistUser(deletedUserId));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());
        verify(artistUserRepository, times(1)).findById(eq(deletedUserId));
    }
    private ArtistUser getArtistUserEntity(CreateArtistUserDto.Request request, Long userId, boolean deleted){
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
    public void givenUpdateArtistUserReq_whenModifiedUser_thenReturnUpdateArtistUserResponse(){
        //given
        final long userId = 1L;
        final UpdateArtistUserDto.Request request = givenUpdateArtistUserReq();
        final ArtistUser artistUserEntity = getArtistUserEntity(userId);
        when(artistUserRepository.findById(any()))
                .thenReturn(
                        Optional.of(artistUserEntity)
                );
        //when
        artistUserService.modifyArtistUser(userId, request);
        //then
        assertThat(artistUserEntity)
            .hasFieldOrPropertyWithValue("nickname",request.getNickname())
            .hasFieldOrPropertyWithValue("bankInfo.bankName",request.getBankName())
            .hasFieldOrPropertyWithValue("bankInfo.accountNumber",request.getAccountNumber())
            .hasFieldOrPropertyWithValue("newsAgency",request.getNewsAgency())
            .hasFieldOrPropertyWithValue("phoneNumber",request.getPhoneNumber())
            .hasFieldOrPropertyWithValue("adultCertification",request.getAdultCertification())
            .hasFieldOrPropertyWithValue("profileImage",request.getProfileImage())
            .hasFieldOrPropertyWithValue("portfolioRoom.portfolioRoomURL",request.getPortfolioUrl())
        ;
        verify(artistUserRepository, times(1)).findById(eq(userId));
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
    private UpdateArtistUserDto.Request givenUpdateArtistUserReq() {
        return UpdateArtistUserDto.Request.builder()
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
    public void givenWrongUserId_whenModifiedUser_thenNorFoundUserId(){
        //given
        final long wrongUserId = 1L;
        final UpdateArtistUserDto.Request request = givenUpdateArtistUserReq();
        when(artistUserRepository.findById(any()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class, () -> artistUserService.modifyArtistUser(wrongUserId, request));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_FOUND_USERID)
                .hasFieldOrPropertyWithValue("errorMessage", UserErrorCode.NOT_FOUND_USERID.getDescription())
        ;
        verify(artistUserRepository, times(1)).findById(eq(wrongUserId));
    }

    @Test
    @DisplayName("[성공] 작가유저 영업마크 변경 ")
    public void givenStatusMarkDto_whenUpdateStatusMark_thenNothing(){
        //given
        final long givenUserId = 1L;
        final StatusMarkDto givenStatusMarkDto = givenStatusMarkDto(StatusMark.REST);
        final ArtistUser artistUser = getArtistUserEntity(givenUserId);
        when(artistUserRepository.findById(anyLong()))
                .thenReturn(
                        Optional.of(
                                artistUser
                        )
                );
        //when //then
        assertThat(artistUser.getStatusMark()).isEqualTo(StatusMark.OPEN);
        artistUserService.updateStatusMark(givenUserId, givenStatusMarkDto);
        assertThat(artistUser.getStatusMark()).isEqualTo(StatusMark.REST);
        verify(artistUserRepository).findById(eq(givenUserId));
    }

    private StatusMarkDto givenStatusMarkDto(StatusMark statusMark) {
        return StatusMarkDto.builder()
                .statusMark(statusMark.name())
                .build();
    }

    @Test
    @DisplayName("[실패] 작가유저 영업마크 변경 - 유저 id가 없는 경우")
    public void givenStatusMarkDto_whenUpdateStatusMarkButNotFoundUser_thenNotFoundUserError(){
        //given
        final long wrongUserId = 1L;
        final StatusMarkDto givenStatusMarkDto = givenStatusMarkDto(StatusMark.REST);
        when(artistUserRepository.findById(any()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        final UserException userException = assertThrows(UserException.class, () -> artistUserService.updateStatusMark(wrongUserId, givenStatusMarkDto));
        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_FOUND_USERID)
                .hasFieldOrPropertyWithValue("errorMessage", UserErrorCode.NOT_FOUND_USERID.getDescription())
        ;
        verify(artistUserRepository, times(1)).findById(eq(wrongUserId));
    }
}