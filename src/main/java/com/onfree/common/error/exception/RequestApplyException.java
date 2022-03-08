package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.RequestApplyErrorCode;
import lombok.Getter;

@Getter
public class RequestApplyException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;


    public RequestApplyException(RequestApplyErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public RequestApplyException(RequestApplyErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
