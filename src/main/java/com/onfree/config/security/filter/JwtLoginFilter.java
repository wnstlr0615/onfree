package com.onfree.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.exception.LoginException;
import com.onfree.config.security.CustomUserDetail;
import com.onfree.config.security.dto.JwtLoginResponse;
import com.onfree.core.dto.LoginFormDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.user.LoginService;
import com.onfree.utils.CookieUtil;
import com.onfree.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.onfree.common.constant.SecurityConstant.ACCESS_TOKEN;
import static com.onfree.common.constant.SecurityConstant.REFRESH_TOKEN;

@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper mapper;
    private final LoginService loginService;
    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public JwtLoginFilter(AuthenticationManager authenticationManager, AuthenticationFailureHandler authenticationFailureHandler, ObjectMapper mapper, LoginService loginService, JWTUtil jwtUtil, CookieUtil cookieUtil) {
        super("/api/v1/login", authenticationManager);
        super.setAuthenticationFailureHandler(authenticationFailureHandler);
        this.mapper = mapper;
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        LoginFormDto loginFormDto;

        try {
             loginFormDto = mapper.readValue(request.getInputStream(), LoginFormDto.class);
        } catch (IOException e) {
            throw new LoginException(LoginErrorCode.WRONG_LOGIN_FORM);
        }

        if(!StringUtils.hasText(loginFormDto.getEmail()) || !StringUtils.hasText(loginFormDto.getPassword())){
            throw new LoginException(LoginErrorCode.EMAIL_OR_PASSWORD_WRONG);
        }

        return this.getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(loginFormDto.getEmail(), loginFormDto.getPassword(), null)
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException{
        final User user = ((CustomUserDetail) authResult.getPrincipal()).getUser();
        createToken(response, user);
    }

    private void createToken(HttpServletResponse response, User user) throws IOException {
        final JwtLoginResponse jwtLoginResponse = getJWTLoginResponse(user);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(
                mapper.writeValueAsString(
                        jwtLoginResponse
                )
        );
        response.addCookie(
                cookieUtil.createCookie(ACCESS_TOKEN, jwtLoginResponse.getAccessToken(), (int)jwtUtil.getAccessTokenExpiredTime())
        );
        response.addCookie(
                cookieUtil.createCookie(REFRESH_TOKEN, jwtLoginResponse.getRefreshToken(), (int)jwtUtil.getRefreshTokenExpiredTime())
        );
        loginService.saveRefreshToken(jwtLoginResponse.getUsername(), jwtLoginResponse.getRefreshToken());
    }

    private JwtLoginResponse getJWTLoginResponse(User user) {
        final String accessToken = jwtUtil.createAccessToken(user);
        final String refreshToken = jwtUtil.createRefreshToken(user);

        return JwtLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getEmail())
                .result(true)
                .build();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        getFailureHandler().onAuthenticationFailure(request,response,failed);
    }

    @Override
    protected AuthenticationFailureHandler getFailureHandler() {
        return super.getFailureHandler();
    }

}
