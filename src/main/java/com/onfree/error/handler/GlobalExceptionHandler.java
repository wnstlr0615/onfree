package com.onfree.error.handler;

import com.onfree.error.exception.UserException;
import com.onfree.error.exception.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity userExceptionHandler(UserException e, HttpServletRequest request){
        log.error("remoteHost: {},  request Url : {}, errorCode : {}",request.getRemoteHost(), request.getRequestURL(), e.getErrorCode());
        return ResponseResult.fail(e.getErrorCode());
    }
}
