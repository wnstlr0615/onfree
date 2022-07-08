package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import com.onfree.config.webmvc.resolver.CurrentArtistUserArgumentResolver;
import com.onfree.controller.user.ArtistUserController;
import com.onfree.core.dto.user.artist.*;
import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.user.ArtistUserService;
import com.onfree.core.service.portfolio.PortfolioService;
import com.onfree.validator.StatusMarkValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistUserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ArtistUserControllerTest extends ControllerBaseTest {
    @MockBean
    ArtistUserService artistUserService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    StatusMarkValidator statusMarkValidator;
    @MockBean
    CurrentArtistUserArgumentResolver currentArtistUserArgumentResolver;

    @Test
    @DisplayName("[성공][POST] 회원가입 요청")
    @WithAnonymousUser
    public void givenCreateUserReq_whenCreateArtistUser_thenCreateUserRes() throws Exception{
        //given
        CreateArtistUserDto.Request request = givenCreateArtistUserReq();
        CreateArtistUserDto.Response response = givenCreateArtistUserRes(request);
        when(artistUserService.addArtistUser(any()))
                .thenReturn(response);

        //when //then
        mvc.perform(post("/api/v1/users/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(request.getName()))
            .andExpect(jsonPath("$.nickname").value(request.getNickname()))
            .andExpect(jsonPath("$.email").value(request.getEmail()))
            .andExpect(jsonPath("$.mobileCarrier").value(request.getMobileCarrier().name()))
            .andExpect(jsonPath("$.phoneNumber").value(request.getPhoneNumber()))
            .andExpect(jsonPath("$.bankName").value(request.getBankName().name()))
            .andExpect(jsonPath("$.accountNumber").value(request.getAccountNumber()))
            .andExpect(jsonPath("$.serviceAgree").value(request.getServiceAgree()))
            .andExpect(jsonPath("$.policyAgree").value(request.getPolicyAgree()))
            .andExpect(jsonPath("$.personalInfoAgree").value(request.getPolicyAgree()))
            .andExpect(jsonPath("$.advertisementAgree").value(request.getAdvertisementAgree()))
            .andExpect(jsonPath("$.adultCertification").value(request.getAdultCertification()))
            .andExpect(jsonPath("$.gender").value(request.getGender().name()))
            .andExpect(jsonPath("$.profileImage").value(request.getProfileImage()))
            .andExpect(jsonPath("$.portfolioUrl").value(request.getPortfolioUrl()))
            
        ;
        verify(artistUserService, times(1)).addArtistUser(any());
    }
    private CreateArtistUserDto.Request givenCreateArtistUserReq() {
        return CreateArtistUserDto.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .nickname("온프리짱짱")
                .name("준식")
                .mobileCarrier(MobileCarrier.SKT)
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
    private CreateArtistUserDto.Response givenCreateArtistUserRes(CreateArtistUserDto.Request request){
        return CreateArtistUserDto.Response
                .builder()
                .adultCertification(request.getAdultCertification())
                .email(request.getEmail())
                .gender(request.getGender())
                .name(request.getName())
                .nickname(request.getNickname())
                .mobileCarrier(request.getMobileCarrier())
                .phoneNumber(request.getPhoneNumber())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .advertisementAgree(request.getAdvertisementAgree())
                .personalInfoAgree(request.getPersonalInfoAgree())
                .policyAgree(request.getPolicyAgree())
                .serviceAgree(request.getServiceAgree())
                .profileImage(request.getProfileImage())
                .portfolioUrl(request.getPortfolioUrl())
                .build();
    }

    private CreateArtistUserDto.Request givenWrongCreateArtistUserReq() {
        return CreateArtistUserDto.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-8888-9999")
                .bankName(null)
                .accountNumber("010-8888-9999")
                .advertisementAgree(true)
                .personalInfoAgree(true)
                .policyAgree(true)
                .serviceAgree(false)
                .profileImage("http://onfree.io/images/123456789")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[실패][POST] 회원가입 요청 - 이메일 중복으로 인한 회원가입 실패")
    public void givenDuplicatedEmail_whenCreateArtistUser_thenDuplicatedEmailError() throws Exception{
        //given
        CreateArtistUserDto.Request request = givenCreateArtistUserReq();
        UserErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;
        when(artistUserService.addArtistUser(any()))
                .thenThrow( new UserException(errorCode));
        //when //then
        mvc.perform(post("/api/v1/users/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )

        ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, times(1)).addArtistUser(any());
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][GET] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        final CreateArtistUserDto.Request request = givenCreateArtistUserReq();

        when(artistUserService.getUserDetail(anyLong()))
                .thenReturn(
                        getArtistUserInfo(request)
                );
        when(currentArtistUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );

        //when //then
        mvc.perform(get("/api/v1/users/artist/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.mobileCarrier").value(request.getMobileCarrier().name()))
                .andExpect(jsonPath("$.phoneNumber").value(request.getPhoneNumber()))
                .andExpect(jsonPath("$.bankName").value(request.getBankName().name()))
                .andExpect(jsonPath("$.accountNumber").value(request.getPhoneNumber()))
                .andExpect(jsonPath("$.serviceAgree").value(request.getServiceAgree()))
                .andExpect(jsonPath("$.policyAgree").value(request.getPolicyAgree()))
                .andExpect(jsonPath("$.personalInfoAgree").value(request.getPolicyAgree()))
                .andExpect(jsonPath("$.advertisementAgree").value(request.getPolicyAgree()))
                .andExpect(jsonPath("$.adultCertification").value(request.getAdultCertification()))
                .andExpect(jsonPath("$.gender").value(request.getGender().name()))
                .andExpect(jsonPath("$.profileImage").value(request.getProfileImage()))
                .andExpect(jsonPath("$.portfolioUrl").value(request.getPortfolioUrl()))
                .andExpect(jsonPath("$.statusMark").value(StatusMark.OPEN.toString()))

        ;
        verify(artistUserService, times(1)).getUserDetail(eq(userId));
    }


    public ArtistUserDetailDto getArtistUserInfo(CreateArtistUserDto.Request request){
            return ArtistUserDetailDto
                    .fromEntity(
                            getArtistUserEntityFromCreateArtistUserRequest(request)
                    );
    }

    public ArtistUser getArtistUserEntityFromCreateArtistUserRequest(CreateArtistUserDto.Request request){
            return request.toEntity();
    }

    @Test
    @WithNormalUser
    @DisplayName("[실패][GET] 사용자 정보 조회 - 일반 유저로 접근 시도")
    public void givenUserId_whenGetUserInfoWithNormUser_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        //when & then
        mvc.perform(get("/api/v1/users/artist/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(artistUserService, never()).getUserDetail(anyLong());
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][DELETE] 사용자 계정 Flag 삭제")
    public void givenDeleteUserId_whenDeleteArtistUser_thenReturnDeletedUserResponse() throws Exception{

        //given
        final long deletedUserId = 1L;
        doNothing().when(artistUserService)
                .removeArtistUser(deletedUserId);
        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );
        //when && then
        mvc.perform(delete("/api/v1/users/artist/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"))
                .andExpect(jsonPath("$.message").value("사용자가 정상적으로 삭제되었습니다."))
        ;
        verify(artistUserService, times(1)).removeArtistUser(anyLong());
    }


    @Test
    @WithNormalUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - 일반 사용자가 접근 하는 경우")
    public void givenDeleteUserId_whenDeleteArtistUserWithNormUser_thenReturnDeletedUserResponse() throws Exception{

        //given
        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );
        //when && then
        mvc.perform(delete("/api/v1/users/artist/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(artistUserService, never()).removeArtistUser(anyLong());
    }


    @Test
    @WithArtistUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - 이미 사용자가 제거된 경우")
    public void givenAlreadyDeleteUserId_whenDeleteArtistUser_thenAlreadyUserDeleted() throws Exception{
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.ALREADY_USER_DELETED;
        doThrow(new UserException(errorCode))
                .when(artistUserService).removeArtistUser(deletedUserId);
        when(currentArtistUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );

        //when && then
        mvc.perform(delete("/api/v1/users/artist/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, times(1)).removeArtistUser(anyLong());
    }
    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 사용자 정보 수정")
    public void givenUpdateUserInfo_whenModifiedUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        doNothing().when(artistUserService)
                .modifyArtistUser(anyLong(), any());
        when(currentArtistUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );

        //when then
        mvc.perform(put("/api/v1/users/artist/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            givenUpdateArtistUserReq()
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("true"))
            .andExpect(jsonPath("$.message").value("사용자 정보가 정상적으로 수정 되었습니다."))
        ;
        verify(artistUserService).modifyArtistUser(eq(userId), any());
    }

    private UpdateArtistUserDto.Response getUpdateArtistUserRes() {
        return UpdateArtistUserDto.Response.builder()
                .nickname("온프리프리")
                .bankName(BankName.IBK)
                .accountNumber("010-0000-0000")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
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
    @Test
    @WithNormalUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 일반 사용자가 접근 할 경우")
    public void givenUpdateUserInfo_whenModifiedUserWithNormalUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );

        //when then
        final ResultActions resultActions = mvc.perform(put("/api/v1/users/artist/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateArtistUserReq()
                        )
                )
        );
        resultActions
                .andDo(print())
                .andExpect(status().isForbidden())
        ;

        verify(artistUserService, never()).modifyArtistUser(eq(userId), any());
    }

    private ArtistUser getArtistUser() {
        final BankInfo bankInfo = BankInfo.builder()
                .accountNumber("010-0000-0000")
                .bankName(BankName.IBK)
                .build();
        UserAgree userAgree = UserAgree.builder()
                .advertisement(true)
                .personalInfo(true)
                .service(true)
                .policy(true)
                .build();
        return ArtistUser.builder()
                .userId(1L)
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon@naver.com")
                .password("{bcrypt}onfree")
                .gender(Gender.MAN)
                .name("joon")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-0000-0000")
                .bankInfo(bankInfo)
                .userAgree(userAgree)
                .adultCertification(true)
                .profileImage("http://www.onfree.co.kr/images/dasdasfasd")
                .deleted(false)
                .role(Role.ARTIST)
                .portfolioUrl("http://www.onfree.co.kr/folioUrl/dasdasfasd")
                .build();
    }

    private UpdateArtistUserDto.Request givenWrongUpdateArtistUserReq() {
        return UpdateArtistUserDto.Request.builder()
                .nickname("온프리프리")
                .accountNumber("010-0000-0000")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .build();
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 영업마크 설정")
    public void givenStatusMark_whenUpdateStatusMark_thenSimpleResponseSuccess() throws Exception{
        //given
        final long givenUserId = 1L;
        final StatusMarkDto givenUpdateStatusMarkDto = givenStatusMarkDto(StatusMark.CLOSE);
        when(statusMarkValidator.supports(any()))
                .thenReturn(true);
        doNothing().when(statusMarkValidator)
                .validate(any(), any());
        doNothing().when(artistUserService)
                .updateStatusMark(anyLong(), any(StatusMarkDto.class));
        when(currentArtistUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);

        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );

        //when //then

        mvc.perform(patch("/api/v1/users/artist/me/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(
                    givenUpdateStatusMarkDto
                )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("영업마크가 성공적으로 변경 되었습니다."))
        ;
        verify(artistUserService).updateStatusMark(eq(givenUserId), any(StatusMarkDto.class));
    }

    private StatusMarkDto givenStatusMarkDto(StatusMark statusMark) {
        return StatusMarkDto.builder()
                .statusMark(statusMark.name())
                .build();
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][PATCH] 작가 유저 닉네임 변경 테스트")
    public void givenNewNickname_whenUpdateNickname_thenSuccess() throws Exception{
        //given
        String updateNickname = "새로운 닉네임";
        UpdateNicknameDto updateNicknameDto = givenUpdateNickname(updateNickname);

        when(currentArtistUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);

        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );
        doNothing()
                .when(artistUserService).updateNickname(anyLong(), anyString());

        //when //then
        mvc.perform(patch("/api/v1/users/artist/me/nickname")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            updateNicknameDto
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("닉네임이 성공적으로 변경되었습니다."))
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(artistUserService).updateNickname(anyLong(), eq(updateNickname));
    }

    private UpdateNicknameDto givenUpdateNickname(String nickname) {
        return UpdateNicknameDto.builder()
                .nickname(nickname)
                .build();
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][PATCH] 변경하려는 닉네임이 이미 있는 경우 - USER_NICKNAME_DUPLICATED_ERROR")
    public void givenDuplicatedNickname_whenUpdateNickname_thenUserNicknameDuplicatedError() throws Exception{
        //given
         UserErrorCode errorCode = UserErrorCode.USER_NICKNAME_DUPLICATED;
        String duplicateNickname = "중복 닉네임";
        UpdateNicknameDto updateNicknameDto = givenUpdateNickname(duplicateNickname);
        when(currentArtistUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);

        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );
        doThrow(new UserException(errorCode))
                .when(artistUserService).updateNickname(anyLong(), anyString());

        //when //then
        mvc.perform(patch("/api/v1/users/artist/me/nickname")
            .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                updateNicknameDto
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
            .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService).updateNickname(anyLong(), eq(duplicateNickname));

    }
}
