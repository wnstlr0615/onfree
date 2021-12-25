package com.onfree.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.config.error.code.LoginErrorCode;
import com.onfree.config.error.exception.LoginException;
import com.onfree.config.security.CustomUserDetail;
import com.onfree.core.dto.LoginFormDto;
import com.onfree.core.entity.user.User;
import com.onfree.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper mapper=new ObjectMapper();
    public JwtLoginFilter(AuthenticationManager authenticationManager, AuthenticationFailureHandler authenticationFailureHandler) {
        super("/login", authenticationManager);
        super.setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        LoginFormDto loginFormDto = new LoginFormDto();
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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        final User user = ((CustomUserDetail) authResult.getPrincipal()).getUser();
        response.setHeader(HttpHeaders.AUTHORIZATION, getJwtToken(user));
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

    private String getJwtToken(User user) {
        final String jwtToken = JWTUtil.createToken(user);
        return "Bearer "+jwtToken;
    }
}
