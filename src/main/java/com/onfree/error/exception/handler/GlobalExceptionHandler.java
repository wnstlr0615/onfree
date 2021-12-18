package com.onfree.error.exception.handler;

import com.onfree.error.exception.UserException;
import com.onfree.error.exception.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = UserException.class)
    public ResponseEntity userExceptionHandler(UserException exception, HttpServletRequest request){
        log.error("requestUrl : {} , errorCode : {}", request.getRequestURI(), exception.getErrorCode());
        return ResponseResult.fail(exception.getErrorCode());
    }
}
