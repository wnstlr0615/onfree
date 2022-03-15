package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.response.FieldErrorDto;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public GlobalException(GlobalErrorCode errorCode, List<FieldError> fieldErrors) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
        this.fieldErrors = getFieldErrorDtos(fieldErrors);
    }

    private List<FieldErrorDto> getFieldErrorDtos(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(FieldErrorDto::fromFieldError)
                .collect(Collectors.toList());
    }
}
