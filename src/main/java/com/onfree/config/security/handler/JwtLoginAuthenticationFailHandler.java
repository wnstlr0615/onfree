package com.onfree.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.exception.LoginException;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.response.SimpleErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Component
public class JwtLoginAuthenticationFailHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper=new ObjectMapper();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        if(exception instanceof  LoginException){
            LoginException loginException=(LoginException)exception;
            log.error(" LoginException - requestRemoteHost : {} , errorCode : {} , errorMessage : {}", request.getRequestURI(), loginException.getErrorCode(), loginException.getMessage());
            responseError(response, loginException.getErrorCode());
        }else if(exception instanceof UsernameNotFoundException){
            log.error(" requestRemoteHost : {} ,UsernameNotFoundException ", request.getRemoteHost());
            responseError(response, LoginErrorCode.EMAIL_OR_PASSWORD_WRONG);
        } else if(exception instanceof BadCredentialsException){
            log.error(" requestRemoteHost : {} ,BadCredentialsException ", request.getRemoteHost());
            responseError(response, LoginErrorCode.EMAIL_OR_PASSWORD_WRONG);
        }else{
            log.error("requestRemoteHost : {},  requestUrl: {}, authenticationException - errorMessage : {}",request.getRemoteHost(), request.getRequestURI(), exception.getMessage());
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
