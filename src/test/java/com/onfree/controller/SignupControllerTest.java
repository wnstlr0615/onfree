package com.onfree.controller;

import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.SignUpException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.service.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SignupController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class SignupControllerTest extends ControllerBaseTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    SignUpService signUpService;


    @Test
    @WithAnonymousUser
    @DisplayName("[성공][GET] 이메일 비동기 인증 시도")
    public void givenEmail_whenAsyncEmailVerification_thenAsync() throws Exception{
        //given
        final String givenEmail = "joon@naver.com";
        doNothing().when(signUpService).asyncEmailVerify(
                eq(givenEmail)
        );

        //when //then
        mvc.perform(
                get("/api/v1/signup/verify/email/{email}", givenEmail)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            ;
        verify(signUpService).asyncEmailVerify(eq(givenEmail));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("[실패][GET] 이메일 비동기 인증 시도 - 공백 입력")
    public void givenBlankEmail_whenAsyncEmailVerification_thenEmailIsBlank() throws Exception{
        //given
        final String givenEmail = " ";
        ErrorCode errorCode = SignUpErrorCode.EMAIL_IS_BLANK;
        doNothing().when(signUpService).asyncEmailVerify(
                eq(givenEmail)
        );

        //when //then
        mvc.perform(
                get("/api/v1/signup/verify/email/{email}", givenEmail)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(signUpService, never()).asyncEmailVerify(eq(givenEmail));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[실패][GET] 이메일 비동기 인증 시도 - 잘못 된 이메일 입력")
    public void givenWrongEmail_whenAsyncEmailVerification_thenEmailIsWrongError() throws Exception{
        //given
        final String givenEmail = "joon@naver.c";
        ErrorCode errorCode = SignUpErrorCode.EMAIL_IS_WRONG;
        doNothing().when(signUpService).asyncEmailVerify(
                eq(givenEmail)
        );

        //when //then
        mvc.perform(
                get("/api/v1/signup/verify/email/{email}", givenEmail)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(signUpService, never()).asyncEmailVerify(eq(givenEmail));
    }

    @Test
    @DisplayName("[성공][GET]이메일 인증 확인")
    public void givenUUID_whenCheckEmailVerify_thenSimpleResponse() throws Exception{
        //given
        final String givenUUID = UUID.randomUUID().toString();
        final String message = "이메일 인증이 완료되었습니다.";
        doNothing().when(signUpService).checkEmailVerify(
                eq(givenUUID)
        );
        //when //then
        mvc.perform(get("/api/v1/signup/verify/uuid/{uuid}", givenUUID)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value(message))
        ;

        verify(signUpService).checkEmailVerify(eq(givenUUID));
    }

    private SimpleResponse givenSimpleResponseOK(String message) {
        return SimpleResponse.success(message);
    }

    @Test
    @DisplayName("[실패][GET]이메일 인증 확인 - uuid가 공백일 경우")
    public void givenBlankUUID_whenCheckEmailVerifyBut_thenUuidIsBlankError() throws Exception{
        //given
        final String givenUUID = " ";
        final SignUpErrorCode errorCode = SignUpErrorCode.UUID_IS_BLANK;

        //when //then
        mvc.perform(get("/api/v1/signup/verify/uuid/{uuid}", givenUUID)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;

        verify(signUpService, never()).checkEmailVerify(eq(givenUUID));
    }

    @Test
    @DisplayName("[실패][GET]이메일 인증 확인 - Redis 에서 uuid KEY를 찾지 못한 경우")
    public void givenUUID_whenCheckEmailVerifyBut_thenExpiredEmailOrWrongUuidError() throws Exception{
        //given
        final String givenUUID = UUID.randomUUID().toString();
        final SignUpErrorCode errorCode = SignUpErrorCode.EXPIRED_EMAIL_OR_WRONG_UUID;
        doThrow(new SignUpException(errorCode)).when(signUpService)
                .checkEmailVerify(
                        eq(givenUUID)
                );
        //when //then
        mvc.perform(get("/api/v1/signup/verify/uuid/{uuid}", givenUUID)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;

        verify(signUpService).checkEmailVerify(eq(givenUUID));
    }


    @Test
    @DisplayName("[성공][GET] 닉네임 중복확인")
    public void givenNickname_whenCheckUserNickname_thenSimpleResponse() throws Exception{
        //given
        final String givenNickname = "온프리짱짱!!";
        final String message = "해당 닉네임은 사용가능합니다.";
        doNothing().when(signUpService).checkUsedNickname(eq(givenNickname));
        //when
        //then
        mvc.perform(get("/api/v1/signup/verify/nickname/{nickname}", givenNickname)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value(message))
        ;
        verify(signUpService).checkUsedNickname(eq(givenNickname));
    }

    @Test
    @DisplayName("[실패][GET] 닉네임 중복확인 - 입력된 닉네임이 공백일 경우")
    public void givenBlankNickname_whenCheckUserNickname_thenNicknameIsBlankError() throws Exception{
        //given
        final String givenNickname = " ";
        final SignUpErrorCode errorCode = SignUpErrorCode.NICKNAME_IS_BLANK;
        //when
        //then
        mvc.perform(get("/api/v1/signup/verify/nickname/{nickname}", givenNickname)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(signUpService, never()).checkUsedNickname(eq(givenNickname));
    }

    @Test
    @DisplayName("[실패][GET] 닉네임 중복확인 - 누군가 사용하고 있는 닉네임이 있을 경우")
    public void givenDuplicatedNickname_whenCheckUserNickname_thenNicknameIsDuplicatedError() throws Exception{
        //given
        final String givenNickname = "온프리짱짱!!";
        final SignUpErrorCode errorCode = SignUpErrorCode.NICKNAME_IS_DUPLICATED;
        doThrow(new SignUpException(errorCode)).when(signUpService).checkUsedNickname(eq(givenNickname));

        //when
        //then
        mvc.perform(get("/api/v1/signup/verify/nickname/{nickname}", givenNickname)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(signUpService).checkUsedNickname(eq(givenNickname));
    }

    @Test
    @DisplayName("[성공][GET] 포트폴리오 개인 URL 중복 확인")
    public void givenPersonalURL_whenCheckPersonalURL_thenSimpleResponse() throws Exception{
        //given
        final String personalUrl = "joon";
        final String message = "해당 URL  은 사용 가능 합니다.";
        doNothing().when(signUpService).checkUsedPersonalURL(
                eq(personalUrl)
        );
        //when
        //then
        mvc.perform(get("/api/v1/signup/verify/personal-url/{personal-url}", personalUrl)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value(message))
        ;
        verify(signUpService).checkUsedPersonalURL(eq(personalUrl));
    }

    @Test
    @DisplayName("[실패][GET] 포트폴리오 개인 URL 중복 확인 - 입력 된 URL이 공백일 경우")
    public void givenBlankPersonalURL_whenCheckPersonalURL_thenPersonalUrlIsBlankError() throws Exception{
        //given
        final String personalUrl = " ";
        final SignUpErrorCode errorCode = SignUpErrorCode.PERSONAL_URL_IS_BLANK;
        //when
        //then
        mvc.perform(get("/api/v1/signup/verify/personal-url/{personal-url}", personalUrl).param("personalUrl", personalUrl)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(signUpService, never()).checkUsedPersonalURL(eq(personalUrl));
    }

    @Test
    @DisplayName("[실패][GET] 포트폴리오 개인 URL 중복 확인 - URL을 누가 사용하고 있을 경우")
    public void givenDuplicatedPersonalURL_whenCheckPersonalURL_thenPersonalUrlIsDuplicatedError() throws Exception{
        //given
        final String personalUrl = "joon";

        final SignUpErrorCode errorCode = SignUpErrorCode.PERSONAL_URL_IS_DUPLICATED;
        doThrow(new SignUpException(errorCode)).when(signUpService).checkUsedPersonalURL(
                eq(personalUrl)
        );

        //when
        //then
        mvc.perform(get("/api/v1/signup/verify/personal-url/{personal-url}", personalUrl)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(signUpService).checkUsedPersonalURL(eq(personalUrl));
    }
}