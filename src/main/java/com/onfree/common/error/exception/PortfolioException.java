package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.PortfolioErrorCode;
import lombok.Getter;

@Getter
public class PortfolioException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public PortfolioException(PortfolioErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public PortfolioException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
