package com.onfree.common.error.exception;

import com.onfree.common.error.code.DrawingFieldErrorCode;
import com.onfree.common.error.code.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class DrawingFieldException extends RuntimeException{
    private DrawingFieldErrorCode errorCode;
    private String errorMessage;

    public DrawingFieldException(DrawingFieldErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public DrawingFieldException(DrawingFieldErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
