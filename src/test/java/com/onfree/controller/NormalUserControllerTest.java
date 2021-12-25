package com.onfree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.config.security.CustomUserDetailService;
import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.normal.CreateNormalUser;
import com.onfree.core.dto.user.normal.NormalUserDetail;
import com.onfree.core.dto.user.normal.UpdateNormalUser;
import com.onfree.core.entity.user.BankName;
import com.onfree.core.entity.user.Gender;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.service.NormalUserService;
import com.onfree.error.code.ErrorCode;
import com.onfree.error.code.UserErrorCode;
import com.onfree.error.exception.UserException;
import com.onfree.utils.Checker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NormalUserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class NormalUserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private NormalUserService normalUserService;

    @MockBean
    private CustomUserDetailService userDetailService;

    @MockBean(name = "checker")
    private Checker checker;

    @Test
    @WithAnonymousUser
    @DisplayName("[성공][POST] 회원가입 요청")
    public void givenCreateUserReq_whenCreateNormalUser_thenCreateUserRes() throws Exception{

        //given
        CreateNormalUser.Request request = givenCreateNormalUserReq();
        CreateNormalUser.Response response = givenCreateNormalUserRes(request);
        when(normalUserService.createdNormalUser(any()))
                .thenReturn(response);

        //when //then
        mvc.perform(post("/api/users/normal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("준식"))
            .andExpect(jsonPath("$.nickname").value("온프리짱짱"))
            .andExpect(jsonPath("$.email").value("jun@naver.com"))
            .andExpect(jsonPath("$.newsAgency").value("SKT"))
            .andExpect(jsonPath("$.phoneNumber").value("010-8888-9999"))
            .andExpect(jsonPath("$.bankName").value(BankName.IBK_BANK.getBankName()))
            .andExpect(jsonPath("$.accountNumber").value("010-8888-9999"))
            .andExpect(jsonPath("$.serviceAgree").value(true))
            .andExpect(jsonPath("$.policyAgree").value(true))
            .andExpect(jsonPath("$.personalInfoAgree").value(true))
            .andExpect(jsonPath("$.advertisementAgree").value(true))
            .andExpect(jsonPath("$.adultCertification").value(true))
            .andExpect(jsonPath("$.gender").value(Gender.MAN.getName()))
            .andExpect(jsonPath("$.profileImage").value("http://onfree.io/images/123456789"))
        ;
        verify(normalUserService, times(1)).createdNormalUser(any());
    }

    @Test
    @WithMockUser(roles = {"ARTIST", "NORMAL"})
    @DisplayName("[실패][POST] 회원가입 요청 - 익명 사용자가 아닌 다른 사람이 접근 할 경우")
    public void givenCreateUserReq_whenCreateNormalUserWithLoginUser_thenCreateUserRes() throws Exception{
        //given
        CreateNormalUser.Request request = givenCreateNormalUserReq();
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when //then
        mvc.perform(post("/api/users/normal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(normalUserService, never()).createdNormalUser(any());
    }
    private CreateNormalUser.Request givenCreateNormalUserReq() {
        return CreateNormalUser.Request
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
                .build();
    }
    private CreateNormalUser.Response givenCreateNormalUserRes(CreateNormalUser.Request request){
        return CreateNormalUser.Response
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
                .build();
    }
    @Test
    @WithAnonymousUser
    @DisplayName("[실패][POST] 회원가입 요청 - 회원가입 request가 올바르지 않은 경우")
    public void givenWrongCreateUserReq_whenCreateNormalUser_thenParameterValidError() throws Exception{
        //given
        CreateNormalUser.Request request = givenWrongCreateNormalUserReq();
        ErrorCode errorCode=UserErrorCode.NOT_VALID_REQUEST_PARAMETERS;
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when //then
        mvc.perform(post("/api/users/normal")
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
        verify(normalUserService, never()).createdNormalUser(any());
    }

    private CreateNormalUser.Request givenWrongCreateNormalUserReq() {
        return CreateNormalUser.Request
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
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[실패][POST] 회원가입 요청 - 이메일 중복으로 인한 회원가입 실패")
    public void givenDuplicatedEmail_whenCreateNormalUser_thenDuplicatedEmailError() throws Exception{
        //given
        CreateNormalUser.Request request = givenCreateNormalUserReq();
        UserErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;
        when(normalUserService.createdNormalUser(any()))
                .thenThrow( new UserException(errorCode));
        //when //then
        mvc.perform(post("/api/users/normal")
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
        verify(normalUserService, times(1)).createdNormalUser(any());
    }

    @Test
    @WithNormalUser
    @DisplayName("[성공][GET] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        when(normalUserService.getUserDetail(userId))
                .thenReturn(
                        getNormalUserInfo()
                );
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when

        //then
        mvc.perform(get("/api/users/normal/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("준식"))
                .andExpect(jsonPath("$.email").value("jun@naver.com"))
                .andExpect(jsonPath("$.newsAgency").value("SKT"))
                .andExpect(jsonPath("$.phoneNumber").value("010-8888-9999"))
                .andExpect(jsonPath("$.bankName").value(BankName.IBK_BANK.getBankName()))
                .andExpect(jsonPath("$.accountNumber").value("010-8888-9999"))
                .andExpect(jsonPath("$.serviceAgree").value(true))
                .andExpect(jsonPath("$.policyAgree").value(true))
                .andExpect(jsonPath("$.personalInfoAgree").value(true))
                .andExpect(jsonPath("$.advertisementAgree").value(true))
                .andExpect(jsonPath("$.adultCertification").value(true))
                .andExpect(jsonPath("$.gender").value(Gender.MAN.getName()))
                .andExpect(jsonPath("$.profileImage").value("http://onfree.io/images/123456789"))
                ;
        verify(normalUserService, times(1)).getUserDetail(any());
    }
    public NormalUserDetail getNormalUserInfo(){
            return NormalUserDetail
                    .fromEntity(
                            getNormalUserEntityFromCreateNormalUserRequest()
                    );
    }

    public NormalUser getNormalUserEntityFromCreateNormalUserRequest(){
            return givenCreateNormalUserReq().toEntity();
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][GET] 사용자 정보 조회 - 작가유저가 접근한 경우")
    public void givenUserId_whenGetUserInfoWithArtistUser_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        when(normalUserService.getUserDetail(userId))
                .thenReturn(
                        getNormalUserInfo()
                );
        //when

        //then
        mvc.perform(get("/api/users/normal/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(normalUserService, never()).getUserDetail(any());
    }

    @Test
    @WithNormalUser
    @DisplayName("[실패][GET] 사용자 정보 조회 - 없는 userId 검색 시 예외발생  ")
    @Disabled("자기 userID가 아니거나 로그인하지 않으면 접근 할 수 없음")
    public void givenWrongUserId_whenGetUserInfo_thenNotFoundUserError() throws Exception {
        //given
        final Long userId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(normalUserService.getUserDetail(userId))
                .thenThrow(new UserException(errorCode));
        //when

        //then
        mvc.perform(get("/api/users/normal/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
        verify(normalUserService, times(1)).getUserDetail(any());

    }
    @Test
    @WithNormalUser
    @DisplayName("[성공][DELETE] 사용자 계정 Flag 삭제")
    public void givenDeleteUserId_whenDeleteNormalUser_thenReturnDeletedUserResponse() throws Exception{

        //given
        final long deletedUserId = 1L;
        when(normalUserService.deletedNormalUser(deletedUserId))
                .thenReturn(getDeletedUserResponse(1L));
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when && then
        mvc.perform(delete("/api/users/normal/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(deletedUserId))
                .andExpect(jsonPath("$.deleted").value(true))
        ;
        verify(normalUserService, times(1)).deletedNormalUser(any());
    }

    private DeletedUserResponse getDeletedUserResponse(long userId) {
        return DeletedUserResponse.builder()
                .userId(userId)
                .deleted(true)
                .build();
    }
    
    @Test
    @WithArtistUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - 작가유저가 접근한 경우")
    public void givenDeleteUserId_whenDeleteNormalUserWithArtistUser_thenReturnDeletedUserResponse() throws Exception{
        //given
        final long deletedUserId = 1L;
        //when && then
        mvc.perform(delete("/api/users/normal/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(normalUserService, never()).deletedNormalUser(any());
    }


    @Test
    @WithNormalUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - userId가 없는 경우")
    @Disabled("자기 userID가 아니거나 로그인하지 않으면 접근 할 수 없음")
    public void givenWrongDeleteUserId_whenDeleteNormalUser_thenNotFoundUserId() throws Exception{
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(normalUserService.deletedNormalUser(deletedUserId))
                .thenThrow(new UserException(errorCode));
        //when && then
        mvc.perform(delete("/api/users/normal/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(normalUserService, times(1)).deletedNormalUser(any());
    }

    @Test
    @WithNormalUser
    @DisplayName("[실패][DELETE] 사용자 계정 Flag 삭제 - 이미 사용자가 제거된 경우")
    public void givenAlreadyDeleteUserId_whenDeleteNormalUser_thenAlreadyUserDeleted() throws Exception{
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.ALREADY_USER_DELETED;
        when(normalUserService.deletedNormalUser(deletedUserId))
                .thenThrow(new UserException(errorCode));
        when(checker.isSelf(anyLong()))
                .thenReturn(true);

        //when && then
        mvc.perform(delete("/api/users/normal/{deletedUserId}", deletedUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(normalUserService, times(1)).deletedNormalUser(any());
    }
    @Test
    @WithNormalUser
    @DisplayName("[성공][PUT] 사용자 정보 수정")
    public void givenUpdateUserInfo_whenModifiedUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        when(normalUserService.modifyedUser(any(), any()))
                .thenReturn(
                        getUpdateNormalUserRes()
                );
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when then
        mvc.perform(put("/api/users/normal/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            givenUpdateNormalUserReq()
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
        verify(normalUserService, times(1)).modifyedUser(eq(userId), any());
    }

    private UpdateNormalUser.Response getUpdateNormalUserRes() {
        return UpdateNormalUser.Response.builder()
                .nickname("온프리프리")
                .bankName(BankName.IBK_BANK)
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
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
    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 작가회원이 접근한 경우")
    public void givenUpdateUserInfo_whenModifiedUserWithArtistUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        //when then
        mvc.perform(put("/api/users/normal/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateNormalUserReq()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(normalUserService, never()).modifyedUser(eq(userId), any());
    }

    @Test
    @WithNormalUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 잘못된 데이터 입력 ")
    public void givenWrongUpdateUserInfo_whenModifiedUser_thenNotValidRequestParametersError() throws Exception{
        //given
        final long userId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_VALID_REQUEST_PARAMETERS;
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when then
        mvc.perform(put("/api/users/normal/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenWrongUpdateNormalUserReq()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
           ;
        verify(normalUserService, never()).modifyedUser(eq(userId), any());
    }

    private UpdateNormalUser.Request givenWrongUpdateNormalUserReq() {
        return UpdateNormalUser.Request.builder()
                .nickname("온프리프리")
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .build();
    }
    @Test
    @WithNormalUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 없는 userId 사용 ")
    public void givenWrongUserId_whenModifiedUser_thenNotValidRequestParametersError() throws Exception{
        //given
        final long wrongUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(normalUserService.modifyedUser(eq(wrongUserId), any()))
                .thenThrow(new UserException(errorCode));
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when then
        mvc.perform(put("/api/users/normal/{userId}", wrongUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateNormalUserReq()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(normalUserService, times(1)).modifyedUser(eq(wrongUserId), any());
    }

}
