package com.onfree.controller;

import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.SignUpException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor
@Slf4j
@RestController
public class SignupController {
    private final SignUpService signUpService;

    /** 이메일 인증 */
    @GetMapping("/api/signup/verify/email")
    public void asyncEmailVerify(
            String email
    ){
        validatedEmail(email);
        signUpService.asyncEmailVerify(email);
    }

    private void validatedEmail(String email) {
        String emailReg = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        if(!email.matches(emailReg)){
            throw new SignUpException(SignUpErrorCode.EMAIL_IS_WRONG);
        }
    }

    /** 이메일 인증 확인 */
    @GetMapping("/api/signup/{uuid}")
    public SimpleResponse checkEmailVerify(@PathVariable("uuid") String uuid){
        return signUpService.checkEmailVerify(uuid);
    }

    /** 닉네임 중복확인 */
    @GetMapping("/api/signup/verify/nickname")
    public SimpleResponse checkUsedNickname(
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
    @GetMapping("/api/signup/verify/personal_url")
    public SimpleResponse checkPersonalURL(String personalUrl){
        return signUpService.checkUsedPersonalURL(personalUrl);
    }

}
