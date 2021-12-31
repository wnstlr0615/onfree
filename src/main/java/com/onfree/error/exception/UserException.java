package com.onfree.error.exception;

import com.onfree.error.code.ErrorCode;
import com.onfree.error.code.UserErrorCode;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;
    List<FieldErrorDto> fieldErrors=new ArrayList<>();

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
    public UserException(UserErrorCode errorCode, List<FieldErrorDto> fieldErrors) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
        this.fieldErrors=fieldErrors;
    }
}
