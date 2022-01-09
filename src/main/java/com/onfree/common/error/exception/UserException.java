package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public UserException(UserErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
