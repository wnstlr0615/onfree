package com.onfree.error.exception;

import com.onfree.error.code.CustomerCenterErrorCode;
import com.onfree.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomerCenterException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public CustomerCenterException(CustomerCenterErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public CustomerCenterException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
