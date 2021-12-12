package com.onfree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.core.dto.NormalUserInfo;
import com.onfree.core.dto.user.CreateNormalUser;
import com.onfree.core.entity.user.BankName;
import com.onfree.core.entity.user.Gender;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.service.UserService;
import com.onfree.error.code.ErrorCode;
import com.onfree.error.code.UserErrorCode;
import com.onfree.error.exception.UserException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NormalUserController.class)
@AutoConfigureMockMvc
class NormalUserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("[성공][POST] 회원가입 요청")
    public void givenCreateUserReq_whenCreateNormalUser_thenCreateUserRes() throws Exception{
        //given
        CreateNormalUser.Request request = givenCreateNormalUserReq();
        CreateNormalUser.Response response = givenCreateNormalUserRes();
        when(userService.createNormalUser(any()))
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
        verify(userService, times(1)).createNormalUser(any());
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
    private CreateNormalUser.Response givenCreateNormalUserRes(){
        return CreateNormalUser.Response
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .gender(Gender.MAN.getName())
                .name("준식")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .bankName(BankName.IBK_BANK.getBankName())
                .accountNumber("010-8888-9999")
                .advertisementAgree(true)
                .personalInfoAgree(true)
                .policyAgree(true)
                .serviceAgree(true)
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }
    @Test
    @DisplayName("[실패][POST] 회원가입 요청 - 회원가입 request가 올바르지 않은 경우")
    public void givenWrongCreateUserReq_whenCreateNormalUser_thenParameterValidError() throws Exception{
        //given
        CreateNormalUser.Request request = givenWrongCreateNormalUserReq();
        ErrorCode errorCode=UserErrorCode.NOT_VALID_REQUEST_PARAMETERS;
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
        verify(userService, never()).createNormalUser(any());
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
    @DisplayName("[실패][POST] 회원가입 요청 - 이메일 중복으로 인한 회원가입 실패")
    public void givenDuplicatedEmail_whenCreateNormalUser_thenDuplicatedEmailError() throws Exception{
        //given
        CreateNormalUser.Request request = givenCreateNormalUserReq();
        ErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;
        when(userService.createNormalUser(any()))
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
        verify(userService, times(1)).createNormalUser(any());
    }

    @Test
    @DisplayName("[성공][GET] 사용자 정보 조회 ")
    public void givenUserId_whenGetUserInfo_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        when(userService.getUserInfo(userId))
                .thenReturn(
                        getNormalUserInfo()
                );
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
        verify(userService, times(1)).getUserInfo(any());
    }
    public NormalUserInfo getNormalUserInfo(){
            return NormalUserInfo
                    .fromEntity(
                            getNormalUserEntityFromCreateNormalUserRequest()
                    );
        }

    public NormalUser getNormalUserEntityFromCreateNormalUserRequest(){
            return givenCreateNormalUserReq().toEntity();
        }
    @Test
    @DisplayName("[실패][GET] 사용자 정보 조회 - 없는 userId 검색 시 예외발생  ")
    public void givenWrongUserId_whenGetUserInfo_thenNotFoundUserError() throws Exception {
        //given
        final Long userId = 1L;
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USERID;
        when(userService.getUserInfo(userId))
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
        verify(userService, times(1)).getUserInfo(any());

    }
}
