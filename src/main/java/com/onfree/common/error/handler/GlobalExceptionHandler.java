package com.onfree.common.error.handler;

import com.onfree.common.error.code.ErrorCode;
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
        printLog(request, "UserException", e.getErrorCode());
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(CustomerCenterException.class)
    public ResponseEntity<?> customerCenterExceptionHandler(CustomerCenterException e, HttpServletRequest request){
        printLog(request, "CustomerCenterException", e.getErrorCode());
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<?> globalExceptionHandler(GlobalException e, HttpServletRequest request){
        printLog(request, "GlobalException", e.getErrorCode());
        if(!e.getFieldErrors().isEmpty()){
            return ResponseResult.fail(e.getErrorCode(), e.getFieldErrors());
        }
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(MailSenderException.class)
    public ResponseEntity<?> mailSenderExceptionHandler(MailSenderException e, HttpServletRequest request){
        printLog(request, "MailSenderException", e.getErrorCode());
        return ResponseResult.fail(e.getErrorCode());
    }
    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<?> mailSenderExceptionHandler(SignUpException e, HttpServletRequest request){
        printLog(request, "SignUpException", e.getErrorCode());
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(DrawingFieldException.class)
    public ResponseEntity<?> drawingFieldExceptionHandler(DrawingFieldException e, HttpServletRequest request){
        printLog(request, "DrawingFieldException", e.getErrorCode());
        return ResponseResult.fail(e.getErrorCode());
    }

    @ExceptionHandler(PortfolioException.class)
    public ResponseEntity<?> portfolioExceptionHandler(PortfolioException e, HttpServletRequest request){
        printLog(request, "PortfolioException", e.getErrorCode());
        return ResponseResult.fail(e.getErrorCode());
    }

    private void printLog(HttpServletRequest request, String exception, ErrorCode errorCode) {
        log.error(exception + ": {}", errorCode);
        log.error("remoteHost: {},  request Url : {}", request.getRemoteHost(), request.getRequestURL());
    }
}
