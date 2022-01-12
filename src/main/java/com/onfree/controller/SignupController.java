package com.onfree.controller;

import com.onfree.common.model.SimpleResponse;
import com.onfree.core.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Validated
@RequiredArgsConstructor
@Slf4j
@RestController
public class SignupController {
    private final SignUpService signUpService;

    /** 이메일 인증 */
    @GetMapping("/test/api/signup/verify/email")
    public void emailVerification(
            @RequestParam
            @Email(message = "이메일 형식이 알맞지 않습니다.")
            String email
    ){
        signUpService.emailVerification(email);
    }

    /** 이메일 인증 확인*/
    @GetMapping("/test/api/signup/{uuid}")
    public SimpleResponse checkEmail(@PathVariable("uuid") String uuid){
        return signUpService.checkEmailVerification(uuid);
    }

    /** 닉네임 중복확인*/
    @GetMapping("/test/api/signup/verify/nickname")
    public SimpleResponse checkUsedNickname(
            @RequestParam
            @NotBlank(message = "NickName 은 공백일 수 없습니다.")
            String nickname
    ){
        return signUpService.checkUsedNickname(nickname);
    }

    /** 포트폴리오룸 개인 URL 중복 확인 */
    @GetMapping("/test/api/signup/verify/personal_url")
    public SimpleResponse checkPersonalURL(@RequestParam String personalUrl){
        return signUpService.checkUsedPersonalURL(personalUrl);
    }

}
