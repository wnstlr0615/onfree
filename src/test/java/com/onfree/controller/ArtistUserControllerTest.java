package com.onfree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.config.CustomUserDetailService;
import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.artist.ArtistUserDetail;
import com.onfree.core.dto.user.artist.CreateArtistUser;
import com.onfree.core.dto.user.artist.UpdateArtistUser;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.BankName;
import com.onfree.core.entity.user.Gender;
import com.onfree.core.service.ArtistUserService;
import com.onfree.error.code.ErrorCode;
import com.onfree.error.code.UserErrorCode;
import com.onfree.error.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistUserController.class)
@AutoConfigureMockMvc
class ArtistUserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ArtistUserService artistUserService;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Test
    @DisplayName("[성공][POST] 회원가입 요청")
    @WithAnonymousUser
    public void givenCreateUserReq_whenCreateArtistUser_thenCreateUserRes() throws Exception{
        //given
        CreateArtistUser.Request request = givenCreateArtistUserReq();
        CreateArtistUser.Response response = givenCreateArtistUserRes(request);
        when(artistUserService.createArtistUser(any()))
                .thenReturn(response);

        //when //then
        mvc.perform(post("/api/users/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(request.getName()))
            .andExpect(jsonPath("$.nickname").value(request.getNickname()))
            .andExpect(jsonPath("$.email").value(request.getEmail()))
            .andExpect(jsonPath("$.newsAgency").value(request.getNewsAgency()))
            .andExpect(jsonPath("$.phoneNumber").value(request.getPhoneNumber()))
            .andExpect(jsonPath("$.bankName").value(request.getBankName().getBankName()))
            .andExpect(jsonPath("$.accountNumber").value(request.getAccountNumber()))
            .andExpect(jsonPath("$.serviceAgree").value(request.getServiceAgree()))
            .andExpect(jsonPath("$.policyAgree").value(request.getPolicyAgree()))
            .andExpect(jsonPath("$.personalInfoAgree").value(request.getPolicyAgree()))
            .andExpect(jsonPath("$.advertisementAgree").value(request.getAdvertisementAgree()))
            .andExpect(jsonPath("$.adultCertification").value(request.getAdultCertification()))
            .andExpect(jsonPath("$.gender").value(request.getGender().getName()))
            .andExpect(jsonPath("$.profileImage").value(request.getProfileImage()))
            .andExpect(jsonPath("$.portfolioUrl").value(request.getPortfolioUrl()))
            
        ;
        verify(artistUserService, times(1)).createArtistUser(any());
    }
    private CreateArtistUser.Request givenCreateArtistUserReq() {
        return CreateArtistUser.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .nickname("온프리짱짱")
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
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .build();
    }
    @Test
    @WithAnonymousUser
    @DisplayName("[실패][POST] 회원가입 요청 - 회원가입 request가 올바르지 않은 경우")
    public void givenWrongCreateUserReq_whenCreateArtistUser_thenParameterValidError() throws Exception{
        //given
        CreateArtistUser.Request request = givenWrongCreateArtistUserReq();
        ErrorCode errorCode=UserErrorCode.NOT_VALID_REQUEST_PARAMETERS;
        //when //then
        mvc.perform(post("/api/users/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, never()).createArtistUser(any());
    }
    private CreateArtistUser.Response givenCreateArtistUserRes(CreateArtistUser.Request request){
        return CreateArtistUser.Response
                .builder()
                .adultCertification(request.getAdultCertification())
                .email(request.getEmail())
                .gender(request.getGender().getName())
                .name(request.getName())
                .nickname(request.getNickname())
                .newsAgency(request.getNewsAgency())
                .phoneNumber(request.getPhoneNumber())
                .bankName(request.getBankName().getBankName())
                .accountNumber(request.getAccountNumber())
                .advertisementAgree(request.getAdvertisementAgree())
                .personalInfoAgree(request.getPersonalInfoAgree())
                .policyAgree(request.getPolicyAgree())
                .serviceAgree(request.getServiceAgree())
                .profileImage(request.getProfileImage())
                .portfolioUrl(request.getPortfolioUrl())
                .build();
    }

    private CreateArtistUser.Request givenWrongCreateArtistUserReq() {
        return CreateArtistUser.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("")
                .newsAgency("SKT")
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
        CreateArtistUser.Request request = givenCreateArtistUserReq();
        ErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;
        when(artistUserService.createArtistUser(any()))
                .thenThrow( new UserException(errorCode));
        //when //then
        mvc.perform(post("/api/users/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, times(1)).createArtistUser(any());
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][GET] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        final CreateArtistUser.Request request = givenCreateArtistUserReq();
        when(artistUserService.getUserDetail(userId))
                .thenReturn(
                        getArtistUserInfo(request)
                );
        //when

        //then
        mvc.perform(get("/api/users/artist/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.newsAgency").value(request.getNewsAgency()))
                .andExpect(jsonPath("$.phoneNumber").value(request.getPhoneNumber()))
                .andExpect(jsonPath("$.bankName").value(request.getBankName().getBankName()))
                .andExpect(jsonPath("$.accountNumber").value(request.getPhoneNumber()))
                .andExpect(jsonPath("$.serviceAgree").value(request.getServiceAgree()))
                .andExpect(jsonPath("$.policyAgree").value(request.getPolicyAgree()))
                .andExpect(jsonPath("$.personalInfoAgree").value(request.getPolicyAgree()))
                .andExpect(jsonPath("$.advertisementAgree").value(request.getPolicyAgree()))
                .andExpect(jsonPath("$.adultCertification").value(request.getAdultCertification()))
                .andExpect(jsonPath("$.gender").value(request.getGender().getName()))
                .andExpect(jsonPath("$.profileImage").value(request.getProfileImage()))
                .andExpect(jsonPath("$.portfolioUrl").value(request.getPortfolioUrl()))

        ;
        verify(artistUserService, times(1)).getUserDetail(any());
    }


    public ArtistUserDetail getArtistUserInfo(CreateArtistUser.Request request){
            return ArtistUserDetail
                    .fromEntity(
                            getArtistUserEntityFromCreateArtistUserRequest(request)
                    );
    }

    public ArtistUser getArtistUserEntityFromCreateArtistUserRequest(CreateArtistUser.Request request){
            return request.toEntity();
    }

    @Test
    @WithNormalUser
    @DisplayName("[실패][GET] 사용자 정보 조회 - 일반 유저로 접근 시도")
    public void givenUserId_whenGetUserInfoWithNormUser_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        //when & then
        mvc.perform(get("/api/users/artist/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(artistUserService, never()).getUserDetail(any());
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][GET] 사용자 정보 조회 - 없는 userId 검색 시 예외발생  ")
    public void givenWrongUserId_whenGetUserInfo_thenNotFoundUserError() throws Exception {
        //given
        final Long userId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(artistUserService.getUserDetail(userId))
                .thenThrow(new UserException(errorCode));
        //when

        //then
        mvc.perform(get("/api/users/artist/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, times(1)).getUserDetail(any());

    }
    @Test
    @WithArtistUser
    @DisplayName("[성공][DELETE] 사용자 계정 Flag 삭제")
    public void givenDeleteUserId_whenDeleteArtistUser_thenReturnDeletedUserResponse() throws Exception{

        //given
        final long deletedUserId = 1L;
        when(artistUserService.deletedArtistUser(deletedUserId))
                .thenReturn(getDeletedUserResponse(1L));
        //when && then
        mvc.perform(delete("/api/users/artist/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(deletedUserId))
                .andExpect(jsonPath("$.deleted").value(true))
        ;
        verify(artistUserService, times(1)).deletedArtistUser(any());
    }

    private DeletedUserResponse getDeletedUserResponse(long userId) {
        return DeletedUserResponse.builder()
                .userId(userId)
                .deleted(true)
                .build();
    }

    @Test
    @WithNormalUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - 일반 사용자가 접근 하는 경우")
    public void givenDeleteUserId_whenDeleteArtistUserWithNormUser_thenReturnDeletedUserResponse() throws Exception{

        //given
        final long deletedUserId = 1L;
        //when && then
        mvc.perform(delete("/api/users/artist/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(artistUserService, never()).deletedArtistUser(any());
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - userId가 없는 경우")
    public void givenWrongDeleteUserId_whenDeleteArtistUser_thenNotFoundUserId() throws Exception{
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(artistUserService.deletedArtistUser(deletedUserId))
                .thenThrow(new UserException(errorCode));
        //when && then
        mvc.perform(delete("/api/users/artist/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, times(1)).deletedArtistUser(any());
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - 이미 사용자가 제거된 경우")
    public void givenAlreadyDeleteUserId_whenDeleteArtistUser_thenAlreadyUserDeleted() throws Exception{
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.ALREADY_USER_DELETED;
        when(artistUserService.deletedArtistUser(deletedUserId))
                .thenThrow(new UserException(errorCode));
        //when && then
        mvc.perform(delete("/api/users/artist/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, times(1)).deletedArtistUser(any());
    }
    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 사용자 정보 수정")
    public void givenUpdateUserInfo_whenModifiedUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        when(artistUserService.modifiedUser(any(), any()))
                .thenReturn(
                        getUpdateArtistUserRes()
                );
        //when then
        mvc.perform(put("/api/users/artist/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            givenUpdateArtistUserReq()
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("온프리프리"))
            .andExpect(jsonPath("$.bankName").value(BankName.IBK_BANK.toString()))
            .andExpect(jsonPath("$.accountNumber").value("010-0000-0000"))
            .andExpect(jsonPath("$.newsAgency").value("SKT"))
            .andExpect(jsonPath("$.phoneNumber").value("010-0000-0000"))
            .andExpect(jsonPath("$.adultCertification").value(true))
            .andExpect(jsonPath("$.profileImage").value("http://onfree.io/images/aaa123"))
        ;
        verify(artistUserService, times(1)).modifiedUser(eq(userId), any());
    }

    private UpdateArtistUser.Response getUpdateArtistUserRes() {
        return UpdateArtistUser.Response.builder()
                .nickname("온프리프리")
                .bankName(BankName.IBK_BANK)
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
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
    @Test
    @WithNormalUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 일반 사용자가 접근 할 경우")
    public void givenUpdateUserInfo_whenModifiedUserWithNormalUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        //when then
        mvc.perform(put("/api/users/artist/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateArtistUserReq()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(artistUserService, never()).modifiedUser(eq(userId), any());
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 잘못된 데이터 입력 ")
    public void givenWrongUpdateUserInfo_whenModifiedUser_thenNotValidRequestParametersError() throws Exception{
        //given
        final long userId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_VALID_REQUEST_PARAMETERS;

        //when then
        mvc.perform(put("/api/users/artist/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenWrongUpdateArtistUserReq()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
           ;
        verify(artistUserService, never()).modifiedUser(eq(userId), any());
    }

    private UpdateArtistUser.Request givenWrongUpdateArtistUserReq() {
        return UpdateArtistUser.Request.builder()
                .nickname("온프리프리")
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .build();
    }
    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 없는 userId 사용 ")
    public void givenWrongUserId_whenModifiedUser_thenNotValidRequestParametersError() throws Exception{
        //given
        final long wrongUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(artistUserService.modifiedUser(eq(wrongUserId), any()))
                .thenThrow(new UserException(errorCode));
        //when then
        mvc.perform(put("/api/users/artist/{userId}", wrongUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateArtistUserReq()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(artistUserService, times(1)).modifiedUser(eq(wrongUserId), any());
    }

}
