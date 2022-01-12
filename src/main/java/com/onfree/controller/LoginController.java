package com.onfree.controller;

import com.onfree.common.model.SimpleResponse;
import com.onfree.config.security.dto.JwtLoginResponse;
import com.onfree.core.dto.LoginFormDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.JWTRefreshTokenService;
import com.onfree.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping( consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "로그인 컨트롤러", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {
    private final JWTRefreshTokenService jwtRefreshTokenService;

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
        jwtRefreshTokenService.deleteTokenByUsername(user.getEmail());
        SecurityContextHolder.clearContext();
        return SimpleResponse.success("로그아웃 성공");
    }

    private void deleteTokenCookie(HttpServletResponse response) {
        Cookie accessCookie = new Cookie(JWTUtil.ACCESS_TOKEN, "");
        accessCookie.setMaxAge(0);
        Cookie refreshCookie = new Cookie(JWTUtil.REFRESH_TOKEN, "");
        refreshCookie.setMaxAge(0);
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

    }
}
