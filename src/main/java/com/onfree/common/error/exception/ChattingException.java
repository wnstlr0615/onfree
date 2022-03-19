package com.onfree.common.error.exception;

import com.onfree.common.error.code.ChattingErrorCode;
import com.onfree.common.error.code.CustomerCenterErrorCode;
import com.onfree.common.error.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChattingException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public ChattingException(ChattingErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public ChattingException(ChattingErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
