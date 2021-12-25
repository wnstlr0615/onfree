package com.onfree.config.error.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.config.error.exception.LoginException;
import com.onfree.error.code.ErrorCode;
import com.onfree.error.code.GlobalErrorCode;
import com.onfree.config.error.code.LoginErrorCode;
import com.onfree.error.exception.response.SimpleErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private ObjectMapper mapper=new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            log.error("requestRemoteHost : {},  requestUrl: {}, authenticationException - errorMessage : {}",request.getRemoteHost(), request.getRequestURI(), authException.getMessage());
            if(authException instanceof LoginException exception){
                responseError(response, exception.getErrorCode());
            }else {
                responseError(response, GlobalErrorCode.UNAUTHORIZED_ERROR);
            }
    }

    private void responseError(HttpServletResponse response,  ErrorCode errorCode) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(errorCode.getStatus());
        response.getWriter().write(
                mapper.writeValueAsString(
                        SimpleErrorResponse.fail(errorCode)
                )
        );
    }
}
