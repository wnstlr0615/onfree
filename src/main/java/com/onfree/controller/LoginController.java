package com.onfree.controller;

import com.onfree.common.dto.SimpleResponse;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.JWTRefreshTokenService;
import com.onfree.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final JWTRefreshTokenService jwtRefreshTokenService;

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
