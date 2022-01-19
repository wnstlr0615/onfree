package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.code.SignUpErrorCode;
import lombok.Getter;

@Getter
public class SignUpException extends RuntimeException{
    ErrorCode errorCode;
    String errorMessage;

    public SignUpException(SignUpErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode=errorCode;
        errorMessage=errorCode.getDescription();
    }

    public SignUpException(SignUpErrorCode errorCode, String message) {
        super(errorCode.getDescription());
        this.errorCode=errorCode;
        errorMessage=message;
    }
}
