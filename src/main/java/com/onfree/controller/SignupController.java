package com.onfree.controller;

import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.SignUpException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.service.SignUpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@Slf4j
@RestController
@Api(tags = "회원가입 인증 컨트롤러")
public class SignupController {
    private final SignUpService signUpService;

    /** 이메일 인증 */
    @ApiOperation(value = "이메일 인증 API", notes = "이메일 인증 API email 주소로 요청 시 이메일 발송 응답없음(비동기)")
    @GetMapping("/api/signup/verify/email")
    public void asyncEmailVerify(
            @ApiParam(value = "이메일 주소", example = "joon@naver.com")
            String email
    ){
        validatedEmail(email);
        signUpService.asyncEmailVerify(email);
    }

    private void validatedEmail(String email) {
        String emailReg = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        if(!StringUtils.hasText(email)){
            throw new SignUpException(SignUpErrorCode.EMAIL_IS_BLANK);
        }
        if(!email.matches(emailReg)){
            throw new SignUpException(SignUpErrorCode.EMAIL_IS_WRONG);
        }
    }

    /** 이메일 인증 확인 */
    @ApiOperation(value = "이메일 인증 확인 API", notes = "발급 받은 uuid를 통해 요청 시 인증 확인 처리")
    @GetMapping("/api/signup/{uuid}")
    public SimpleResponse checkEmailVerify(
            @ApiParam(value = "이메일 확인 인증 uuid", example = "123123-54654-54123-21344")
            @PathVariable("uuid") String uuid)
    {
        validatedUUID(uuid);
        return signUpService.checkEmailVerify(uuid);
    }

    private void validatedUUID(String uuid) {
        if(!StringUtils.hasText(uuid)){
            throw new SignUpException(SignUpErrorCode.UUID_IS_BLANK);
        }
    }

    /** 닉네임 중복확인 */
    @ApiOperation(value = "닉네임 중복 확인 API", notes = "닉네임 중복확인 API")
    @GetMapping("/api/signup/verify/nickname")
    public SimpleResponse checkUsedNickname(
            @ApiParam(value = "닉네임",  example = "joon")
            String nickname
    ){
        validatedNickname(nickname);
        return signUpService.checkUsedNickname(nickname);
    }

    private void validatedNickname(String nickname) {
        if(!StringUtils.hasText(nickname)){
            throw new SignUpException(SignUpErrorCode.NICKNAME_IS_BLANK);
        }
    }

    /** 포트폴리오룸 개인 URL 중복 확인 */
    @ApiOperation(value = "포트폴리오룸 개인 URL 중복 확인 API", notes = "포트폴리오룸 개인 URL 중복 확인 API")
    @GetMapping("/api/signup/verify/personal_url")
    public SimpleResponse checkPersonalURL(
            @ApiParam(value = "포트폴리오룸 개인 URL ", example = "joon")
            String personalUrl
    ){
        validatedPersonalUrl(personalUrl);
        return signUpService.checkUsedPersonalURL(personalUrl);
    }

    private void validatedPersonalUrl(String personalUrl) {
        if(!StringUtils.hasText(personalUrl)){
            throw new SignUpException(SignUpErrorCode.PERSONAL_URL_IS_BLANK);
        }
    }

}
