package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.response.FieldErrorDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GlobalException extends RuntimeException{
    ErrorCode errorCode;
    String errorMessage;
    List<FieldErrorDto> fieldErrors=new ArrayList<>();

    public GlobalException(GlobalErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public GlobalException(GlobalErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
    public GlobalException(GlobalErrorCode errorCode, List<FieldErrorDto> fieldErrors) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
        this.fieldErrors=fieldErrors;
    }
}
