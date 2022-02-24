package com.onfree.controller;

import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.LoginException;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.dto.user.UpdatePasswordDto;
import com.onfree.core.service.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class LoginControllerTest extends ControllerBaseTest {

    @MockBean
    LoginService loginService;

    @Test
    @DisplayName("[성공][GET] 비밀번호 인증용 메일 전송 ")
    public void givenEmail_whenPasswordResetSendMail_thenSuccess() throws Exception{
        //given
        final String email = "wnstlr0615@naver.com";
        Mockito.doNothing()
                .when(loginService).passwordReset(eq(email));
        //when //then
        mvc.perform(get("/api/v1/password/reset")
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("패스워드 초기화 인증 메일을 전송하였습니다."))
        ;
        verify(loginService).passwordReset(eq(email));
    }

    @Test
    @DisplayName("[실패][GET] 비밀번호 인증용 메일 전송 - 입력된 이메일이 공백일 경우")
    public void givenEmptyEmail_whenPasswordResetSendMail_thenNotValidatedRequestError() throws Exception{
        //given
        final String email = "";
        final GlobalErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;
        //when //then
        mvc.perform(get("/api/v1/password/reset")
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(loginService, never()).passwordReset(eq(email));
    }

    @Test
    @DisplayName("[실패][GET] 비밀번호 인증용 메일 전송 - 입력된 이메일이 공백일 경우")
    public void givenNotValidEmail_whenPasswordResetSendMail_thenNotValidatedRequestError() throws Exception{
        //given
        final String email = "wnstlr0615@naver.c";
        final GlobalErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;
        //when //then
        mvc.perform(get("/api/v1/password/reset")
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(loginService, never()).passwordReset(eq(email));
    }
    @Test
    @DisplayName("[실패][GET] 비밀번호 인증용 메일 전송 - 해당 이메일을 가진 유저가 없는 경우")
    public void givenEmail_whenPasswordResetSendMailButNotFoundUser_thenNotValidatedRequestError() throws Exception{
        //given
        final String email = "wnstlr0615@naver.com";
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USER_EMAIL;
        doThrow(new UserException(errorCode))
                .when(loginService).passwordReset(eq(email));
        //when //then
        mvc.perform(get("/api/v1/password/reset")
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(loginService).passwordReset(eq(email));
    }


    @Test
    @DisplayName("[성공][POST] 이메일 인증 후 비밀번호 재설정")
    public void givenPasswordUpdateDto_whenUpdatePassword_thenSuccess() throws Exception{
        //given
        UpdatePasswordDto updatePasswordDto = givenPasswordUpdateDto("uuid", "newPassword");
        doNothing().when(loginService).updatePassword(any(UpdatePasswordDto.class));

        //when//then
        mvc.perform(post("/api/v1/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                updatePasswordDto
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isOk())
        ;
        verify(loginService).updatePassword(any(UpdatePasswordDto.class));
    }

    private UpdatePasswordDto givenPasswordUpdateDto(String uuid, String newPassword) {
        return UpdatePasswordDto.builder()
                .uuid(uuid)
                .newPassword(newPassword)
                .build();
    }

    @Test
    @DisplayName("[실패][POST] 이메일 인증 후 비밀번호 재설정 - uuid 토큰이 만료된 경우")
    public void givenExpiredPasswordResetUuid_whenUpdatePassword_thenExpiredPasswordResetUuidError() throws Exception{
        //given
        UpdatePasswordDto updatePasswordDto = givenPasswordUpdateDto("expired uuid", "newPassword");
        final LoginErrorCode errorCode = LoginErrorCode.EXPIRED_PASSWORD_RESET_UUID;
        doThrow(new LoginException(LoginErrorCode.EXPIRED_PASSWORD_RESET_UUID)).when(loginService)
                .updatePassword(any(UpdatePasswordDto.class));

        //when//then
        mvc.perform(post("/api/v1/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                updatePasswordDto
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(loginService).updatePassword(any(UpdatePasswordDto.class));
    }

    @Test
    @DisplayName("[실패][POST] 이메일 인증 후 비밀번호 재설정 - 해당 토큰에 대한 사용자가 없는 경우")
    public void givenUpdatePassword_whenUpdatePasswordButNotFoundUser_thenNotFoundUserEmailError() throws Exception{
        //given
        UpdatePasswordDto updatePasswordDto = givenPasswordUpdateDto("expired uuid", "newPassword");
        final UserErrorCode errorCode = UserErrorCode.NOT_FOUND_USER_EMAIL;
        doThrow(new UserException(errorCode)).when(loginService)
                .updatePassword(any(UpdatePasswordDto.class));

        //when//then
        mvc.perform(post("/api/v1/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                updatePasswordDto
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(loginService).updatePassword(any(UpdatePasswordDto.class));
    }

}