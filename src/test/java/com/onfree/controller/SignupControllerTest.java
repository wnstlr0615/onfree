package com.onfree.controller;

import com.onfree.common.WebMvcBaseTest;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.SignUpException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.service.AwsS3Service;
import com.onfree.core.service.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SignupController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class SignupControllerTest extends WebMvcBaseTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    SignUpService signUpService;
    @MockBean
    AwsS3Service awsS3Service;

    @Test
    @DisplayName("[성공][POST] 프로필 사진 업로드")
    public void givenImageMultipartFile_whenProfileImageUpload_thenFileAccessUrl() throws Exception{
        //given
        final String fileUrl = "https://s3.ap-northeast-2.amazonaws.com/onfree-store/users/profileImage/8c2ac333-9b9b-4c01-867b-c245b1fa65fd.PNG";
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.png","image/png", "test".getBytes(StandardCharsets.UTF_8));
        when(awsS3Service.s3ProfileImageFileUpload(any()))
                .thenReturn(fileUrl);
        //when
        //then
        mvc.perform(multipart("/api/signup/profileImage")
                .file(file)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(fileUrl))
        ;
        verify(awsS3Service).s3ProfileImageFileUpload(any());
    }

    @Test
    @DisplayName("[실패][POST] 프로필 사진 업로드 - 파일이 비었을 경우")
    public void givenImageEmptyMultipartFile_whenProfileImageUpload_thenFileIsEmptyError() throws Exception{
        //given
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.png","image/png", (byte[]) null);
        final SignUpErrorCode errorCode = SignUpErrorCode.FILE_IS_EMPTY;

        //when //then
        mvc.perform(multipart("/api/signup/profileImage")
                .file(file)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(awsS3Service, never()).s3ProfileImageFileUpload(any());
    }

    @Test
    @DisplayName("[실패][POST] 프로필 사진 업로드 - 파일 확장자를 지원 하지 않는 경우")
    public void givenNotAllowMultipartFile_whenProfileImageUpload_thenFileIsEmptyError() throws Exception{
        //given
        final MockMultipartFile file = new MockMultipartFile("file", "aaaa.csv","image/png", "test".getBytes(StandardCharsets.UTF_8));
        final SignUpErrorCode errorCode = SignUpErrorCode.NOT_ALLOW_FILE_TYPE;
        //when//then
        mvc.perform(multipart("/api/signup/profileImage")
                .file(file)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(awsS3Service, never()).s3ProfileImageFileUpload(any());

    }

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
        mvc.perform(get("/api/signup/verify/email")
                .queryParam("email", givenEmail)
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
        final String givenEmail = "";
        ErrorCode errorCode = SignUpErrorCode.EMAIL_IS_BLANK;
        doNothing().when(signUpService).asyncEmailVerify(
                eq(givenEmail)
        );

        //when //then
        mvc.perform(get("/api/signup/verify/email")
                .queryParam("email", givenEmail)
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
        mvc.perform(get("/api/signup/verify/email")
                .queryParam("email", givenEmail)
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
        when(signUpService.checkEmailVerify(
                eq(givenUUID)
        )).thenReturn(
                givenSimpleResponseOK(message)
        );
        //when //then
        mvc.perform(get("/api/signup/{uuid}", givenUUID))
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
        mvc.perform(get("/api/signup/{uuid}", givenUUID))
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
        when(signUpService.checkEmailVerify(
                eq(givenUUID)
        )).thenThrow(
                new SignUpException(errorCode)
        );
        //when //then
        mvc.perform(get("/api/signup/{uuid}", givenUUID))
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
        final String message = "사용가능한 닉네임입니다.";
        when(signUpService.checkUsedNickname(
                eq(givenNickname)
        )).thenReturn(givenSimpleResponseOK(message));
        //when
        //then
        mvc.perform(get("/api/signup/verify/nickname")
            .param("nickname", givenNickname)
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
        final String givenNickname = "";
        final SignUpErrorCode errorCode = SignUpErrorCode.NICKNAME_IS_BLANK;
        //when
        //then
        mvc.perform(get("/api/signup/verify/nickname")
                .param("nickname", givenNickname)
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
        when(signUpService.checkUsedNickname(
                eq(givenNickname)
        )).thenThrow(new SignUpException(errorCode));
        //when
        //then
        mvc.perform(get("/api/signup/verify/nickname")
                .param("nickname", givenNickname)
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
        final String message = "사용 가능한 URL 입니다.";
        when(signUpService.checkUsedPersonalURL(
                eq(personalUrl)
        )).thenReturn(givenSimpleResponseOK(message));
        //when
        //then
        mvc.perform(get("/api/signup/verify/personal_url")
                .param("personalUrl", personalUrl)
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
        final String personalUrl = "";
        final SignUpErrorCode errorCode = SignUpErrorCode.PERSONAL_URL_IS_BLANK;
        //when
        //then
        mvc.perform(get("/api/signup/verify/personal_url")
                .param("personalUrl", personalUrl)
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
        when(signUpService.checkUsedPersonalURL(
                eq(personalUrl)
        )).thenThrow(new SignUpException(errorCode));
        //when
        //then
        mvc.perform(get("/api/signup/verify/personal_url")
                .param("personalUrl", personalUrl)
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(signUpService).checkUsedPersonalURL(eq(personalUrl));
    }
}