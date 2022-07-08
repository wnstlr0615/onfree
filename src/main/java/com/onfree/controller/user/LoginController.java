package com.onfree.controller.user;

import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.config.security.dto.JwtLoginResponse;
import com.onfree.core.dto.LoginFormDto;
import com.onfree.core.dto.user.UpdatePasswordDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.user.LoginService;
import com.onfree.utils.CookieUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.onfree.common.constant.SecurityConstant.ACCESS_TOKEN;
import static com.onfree.common.constant.SecurityConstant.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class LoginController {
    private final LoginService loginService;
    private final CookieUtil cookieUtil;
    @ApiOperation(value = "로그인", notes = "로그인 요청")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtLoginResponse login(@RequestBody LoginFormDto loginFormDto){
        return JwtLoginResponse.builder().build(); //문서를 남기기 위한 메소드이므로 로그인은 필터에서 처리
    }

    @ApiOperation(value = "로그아웃", notes = "로그인 사용자가 로그아웃 요청")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse logout(HttpServletResponse response){
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        deleteTokenCookie(response);
        loginService.deleteRefreshTokenByUsername(user.getEmail());
        SecurityContextHolder.clearContext();
        return SimpleResponse.success("로그아웃 성공");
    }

    private void deleteTokenCookie(HttpServletResponse response) {
        Cookie accessCookie = cookieUtil.resetCookie(ACCESS_TOKEN);
        Cookie refreshCookie = cookieUtil.resetCookie(REFRESH_TOKEN);
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);


    }

    @ApiOperation(value = "비밀번호 인증용 메일 전송 API", notes = "메일인증을 통하여 패스워드 초기화 링크 전송")
    @GetMapping("/password/reset")
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
    @PostMapping(value = "/password/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse updatePassword(
            @Valid @RequestBody UpdatePasswordDto updatePasswordDto
    ){
        loginService.updatePassword(updatePasswordDto);
        return SimpleResponse.success("비밀번호 변경이 완료되었습니다.");
    }

    @GetMapping("/password/uuid/{uuid}")
    public String certificationLink(@PathVariable String uuid){
        return "반환 uuid 와 새로운 비밀번호 입력 페이지 만들어서 링크 알려주시면 해당 링크로 변경해드릴게요.";
    }

}
