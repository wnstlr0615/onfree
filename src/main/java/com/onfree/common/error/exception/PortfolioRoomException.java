package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.PortfolioErrorCode;
import com.onfree.common.error.code.PortfolioRoomErrorCode;
import lombok.Getter;

@Getter
public class PortfolioRoomException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public PortfolioRoomException(PortfolioRoomErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public PortfolioRoomException(PortfolioRoomErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
