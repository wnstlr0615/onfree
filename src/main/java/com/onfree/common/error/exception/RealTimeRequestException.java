package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.RealTimeRequestErrorCode;
import lombok.Getter;

@Getter
public class RealTimeRequestException extends RuntimeException{
    ErrorCode errorCode;
    String errorMessage;


    public RealTimeRequestException(RealTimeRequestErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public RealTimeRequestException(RealTimeRequestErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
