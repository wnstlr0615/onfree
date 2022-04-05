package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.TossPaymentErrorCode;
import com.onfree.core.dto.external.toss.TossErrorRes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TossPaymentException extends RuntimeException{
    ErrorCode errorCode;
    String errorMessage;

    public TossPaymentException(TossPaymentErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public TossPaymentException(TossPaymentErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
