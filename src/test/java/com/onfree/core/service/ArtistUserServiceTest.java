package com.onfree.core.service;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.dto.user.artist.ArtistUserDetailDto;
import com.onfree.core.dto.user.artist.CreateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.ArtistUserRepository;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ArtistUserServiceTest {

    @Mock
    ArtistUserRepository artistUserRepository;
    @Mock
    UserRepository userRepository;
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
        when(artistUserRepository.countByEmail(anyString()))
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
                .hasFieldOrPropertyWithValue("gender", userReq.getGender())
                .hasFieldOrPropertyWithValue("name", userReq.getName())
                .hasFieldOrPropertyWithValue("nickname", userReq.getNickname())
                .hasFieldOrPropertyWithValue("mobileCarrier", userReq.getMobileCarrier())
                .hasFieldOrPropertyWithValue("phoneNumber", userReq.getPhoneNumber())
                .hasFieldOrPropertyWithValue("bankName", userReq.getBankName())
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
                .mobileCarrier(MobileCarrier.KT)
                .phoneNumber("010-8888-9999")
                .bankName(BankName.IBK)
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
        when(artistUserRepository.findByUserIdAndDeletedIsFalse(anyLong()))
                .thenReturn(
                        Optional.ofNullable(
                                getArtistUserEntity(userId)
                        )
                );
        //when
        final ArtistUserDetailDto userInfo = artistUserService.getUserDetail(userId);
        //then
        assertThat(userInfo)
                .hasFieldOrPropertyWithValue("adultCertification", true)
                .hasFieldOrPropertyWithValue("email", "jun@naver.com")
                .hasFieldOrPropertyWithValue("gender", Gender.MAN)
                .hasFieldOrPropertyWithValue("name", "준식")
                .hasFieldOrPropertyWithValue("nickname", "joon")
                .hasFieldOrPropertyWithValue("mobileCarrier", MobileCarrier.SKT)
                .hasFieldOrPropertyWithValue("phoneNumber", "010-8888-9999")
                .hasFieldOrPropertyWithValue("bankName", BankName.IBK)
                .hasFieldOrPropertyWithValue("accountNumber", "010-8888-9999")
                .hasFieldOrPropertyWithValue("advertisementAgree", true)
                .hasFieldOrPropertyWithValue("personalInfoAgree", true)
                .hasFieldOrPropertyWithValue("policyAgree", true)
                .hasFieldOrPropertyWithValue("serviceAgree", true)
                .hasFieldOrPropertyWithValue("profileImage", "http://onfree.io/images/123456789")
                .hasFieldOrPropertyWithValue("portfolioUrl", "http://onfree.io/portfolioUrl/123456789")
        ;
    }

    @Test
    @DisplayName("[성공] 사용자 계정 삭제")
    public void givenDeletedUserId_whenDeletedUser_thenDeleteUserResponse(){
        //given
        final long deletedUserId = 1L;
        when(artistUserRepository.findByUserIdAndDeletedIsFalse(deletedUserId))
                .thenReturn(
                        Optional.of(getArtistUserEntity(
                                givenCreateArtistUserReq(), deletedUserId
                        ))
                );
        //when
        artistUserService.removeArtistUser(deletedUserId);

        //then
        verify(artistUserRepository, times(1)).findByUserIdAndDeletedIsFalse(eq(deletedUserId));
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

        when(artistUserRepository.findByUserIdAndDeletedIsFalse(deletedUserId))
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
        verify(artistUserRepository, times(1)).findByUserIdAndDeletedIsFalse(eq(deletedUserId));
    }

    @Test
    @DisplayName("[실패] 사용자 계정 삭제 - 이미 삭제된 계정인 경우")
    public void givenAlreadyDeletedUserId_whenDeletedUser_thenAlreadyUserDeleted(){
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.ALREADY_USER_DELETED;
        final boolean deleted = true;

        when(artistUserRepository.findByUserIdAndDeletedIsFalse(deletedUserId))
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
        verify(artistUserRepository, times(1)).findByUserIdAndDeletedIsFalse(eq(deletedUserId));
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
                .mobileCarrier(request.getMobileCarrier())
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
        when(artistUserRepository.findByUserIdAndDeletedIsFalse(any()))
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
            .hasFieldOrPropertyWithValue("mobileCarrier",request.getMobileCarrier())
            .hasFieldOrPropertyWithValue("phoneNumber",request.getPhoneNumber())
            .hasFieldOrPropertyWithValue("adultCertification",request.getAdultCertification())
            .hasFieldOrPropertyWithValue("profileImage",request.getProfileImage())
            .hasFieldOrPropertyWithValue("portfolioRoom.portfolioRoomURL",request.getPortfolioUrl())
        ;
        verify(artistUserRepository, times(1)).findByUserIdAndDeletedIsFalse(eq(userId));
    }
    public ArtistUser getArtistUserEntity(long userId){
        return ArtistUser.builder()
                .userId(userId)
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .nickname("joon")
                .name("준식")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-8888-9999")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .bankInfo(
                        getBankInfo(BankName.IBK, "010-8888-9999")
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
                .bankName(BankName.IBK)
                .accountNumber("010-0000-0000")
                .mobileCarrier(MobileCarrier.SKT)
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
    @DisplayName("[성공] 작가유저 영업마크 변경 ")
    public void givenStatusMarkDto_whenUpdateStatusMark_thenNothing(){
        //given
        final long givenUserId = 1L;
        final StatusMarkDto givenStatusMarkDto = givenStatusMarkDto(StatusMark.REST);
        final ArtistUser artistUser = getArtistUserEntity(givenUserId);
        when(artistUserRepository.findByUserIdAndDeletedIsFalse(anyLong()))
                .thenReturn(
                        Optional.of(
                                artistUser
                        )
                );
        //when //then
        assertThat(artistUser.getStatusMark()).isEqualTo(StatusMark.OPEN);
        artistUserService.updateStatusMark(givenUserId, givenStatusMarkDto);
        assertThat(artistUser.getStatusMark()).isEqualTo(StatusMark.REST);
        verify(artistUserRepository).findByUserIdAndDeletedIsFalse(eq(givenUserId));
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
        when(artistUserRepository.findByUserIdAndDeletedIsFalse(any()))
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
        verify(artistUserRepository, times(1)).findByUserIdAndDeletedIsFalse(eq(wrongUserId));
    }
    
    @Test
    @DisplayName("[성공] 작가유저 닉네임 변경")
    public void givenNewNickname_whenUpdateNickname_thenNothing(){
        //given
        String newNickname = "새로운 닉네임";
        long userId = 1L;

        ArtistUser artistUserEntity = getArtistUserEntity(userId);
        String beforeNickname = artistUserEntity.getNickname();

        when(userRepository.countByNickname(anyString()))
                .thenReturn(0);

        when(artistUserRepository.findByUserIdAndDeletedIsFalse(anyLong()))
                .thenReturn(
                        Optional.of(
                                artistUserEntity
                        )
                );

        //when
        artistUserService.updateNickname(userId, newNickname);

        //then
        assertAll(
            () -> assertThat(beforeNickname).isNotEqualTo(artistUserEntity.getNickname()),
            () -> assertThat(artistUserEntity.getNickname()).isEqualTo(newNickname)
        );
        verify(userRepository).countByNickname(eq(newNickname));
        verify(artistUserRepository).findByUserIdAndDeletedIsFalse(eq(userId));

    }

    @Test
    @DisplayName("[실패] 닉네임 중복으로 인한 닉네임 변경 실패")
    public void givenDuplicatedNewNickname_whenUpdateNickname_thenNothing(){
        //given
        String duplicatedNickname = "중복 닉네임";
        long userId = 1L;

        ArtistUser artistUserEntity = getArtistUserEntity(userId);

        ErrorCode errorCode = UserErrorCode.USER_NICKNAME_DUPLICATED;
        when(userRepository.countByNickname(anyString()))
                .thenReturn(1);

        //when
        UserException userException = assertThrows(
                UserException.class,
                () -> artistUserService.updateNickname(userId, duplicatedNickname)
        );

        //then
        assertThat(userException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription());

        verify(userRepository).countByNickname(eq(duplicatedNickname));
        verify(artistUserRepository, never()).findByUserIdAndDeletedIsFalse(eq(userId));

    }
}