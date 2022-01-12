package com.onfree.common.error.exception;

import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.code.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;


@Getter
public class LoginException extends AuthenticationException {
    ErrorCode errorCode;
    String errorMessage;

    public LoginException(LoginErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode=errorCode;
        errorMessage=errorCode.getDescription();
    }

    public LoginException(ErrorCode errorCode, String message) {
        super(errorCode.getDescription());
        this.errorCode=errorCode;
        errorMessage=message;
    }
}
