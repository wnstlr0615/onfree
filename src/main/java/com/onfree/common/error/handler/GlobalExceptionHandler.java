package com.onfree.common.error.handler;

import com.onfree.common.error.exception.*;
import com.onfree.common.error.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?>  userExceptionHandler(UserException e, HttpServletRequest request){
        log.error("UserException  : {}", e.getErrorCode());
        log.error("remoteHost: {},  request Url : {}",request.getRemoteHost(), request.getRequestURL());
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(CustomerCenterException.class)
    public ResponseEntity<?> customerCenterExceptionHandler(CustomerCenterException e, HttpServletRequest request){
        log.error("CustomerCenterException  : {}", e.getErrorCode());
        log.error("remoteHost: {},  request Url : {}",request.getRemoteHost(), request.getRequestURL());
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<?> globalExceptionHandler(GlobalException e, HttpServletRequest request){
        log.error("GlobalException  : {}", e.getErrorCode());
        log.error("remoteHost: {},  request Url : {}",request.getRemoteHost(), request.getRequestURL());
        if(!e.getFieldErrors().isEmpty()){
            return ResponseResult.fail(e.getErrorCode(), e.getFieldErrors());
        }
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(MailSenderException.class)
    public ResponseEntity<?> mailSenderExceptionHandler(MailSenderException e, HttpServletRequest request){
        log.error("MailSenderException  : {}", e.getErrorCode());
        log.error("remoteHost: {},  request Url : {}",request.getRemoteHost(), request.getRequestURL());
        return ResponseResult.fail(e.getErrorCode());
    }
    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<?> mailSenderExceptionHandler(SignUpException e, HttpServletRequest request){
        log.error("SignUpException  : {}", e.getErrorCode());
        log.error("remoteHost: {},  request Url : {}",request.getRemoteHost(), request.getRequestURL());
        return ResponseResult.fail(e.getErrorCode());
    }
}
