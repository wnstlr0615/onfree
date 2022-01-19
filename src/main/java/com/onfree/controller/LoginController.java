package com.onfree.controller;

import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.config.security.dto.JwtLoginResponse;
import com.onfree.core.dto.LoginFormDto;
import com.onfree.core.dto.user.UpdatePasswordDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.onfree.common.constant.SecurityConstant.ACCESS_TOKEN;
import static com.onfree.common.constant.SecurityConstant.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping( consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "로그인 컨트롤러", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {
    private final LoginService loginService;

    @ApiOperation(value = "로그인", notes = "로그인 요청")
    @PostMapping("/login")
    public JwtLoginResponse login(@RequestBody LoginFormDto loginFormDto){
        return JwtLoginResponse.builder().build(); //문서를 남기기 위한 메소드이므로 로그인은 필터에서 처리
    }

    @ApiOperation(value = "로그아웃", notes = "로그인 사용자가 로그아웃 요청")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/logout")
    public SimpleResponse logout(HttpServletResponse response){
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        deleteTokenCookie(response);
        loginService.deleteRefreshTokenByUsername(user.getEmail());
        SecurityContextHolder.clearContext();
        return SimpleResponse.success("로그아웃 성공");
    }

    private void deleteTokenCookie(HttpServletResponse response) {
        Cookie accessCookie = new Cookie(ACCESS_TOKEN, "");
        accessCookie.setMaxAge(0);
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN, "");
        refreshCookie.setMaxAge(0);
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

    }
    @ApiOperation(value = "비밀번호 인증용 메일 전송 API", notes = "메일인증을 통하여 패스워드 초기화 링크 전송")
    @GetMapping("/api/password/reset")
    public SimpleResponse passwordResetSendMail(@RequestParam String email){
        validatedEmail(email);
        loginService.passwordReset(email);
        return SimpleResponse.success("패스워드 초기화 인증 메일을 전송하였습니다.");
    }
    private void validatedEmail(String email) {
        String emailReg = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        if(!StringUtils.hasText(email) || !email.matches(emailReg)){
            throw new GlobalException(GlobalErrorCode.NOT_VALIDATED_REQUEST);
        }
    }
    @ApiOperation(value = "메일을 통한 인증 후 비밀번호 재설정 API", notes = "메일 인증후 패스워드 변경")
    @PostMapping("/api/password/reset")
    public SimpleResponse updatePassword(
            @Valid @RequestBody UpdatePasswordDto updatePasswordDto,
            BindingResult errors
    ){
        loginService.updatePassword(updatePasswordDto);
        return SimpleResponse.success("비밀번호 변경이 완료되었습니다.");
    }



}
