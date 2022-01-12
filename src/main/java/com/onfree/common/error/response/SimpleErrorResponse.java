package com.onfree.common.error.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onfree.common.error.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleErrorResponse {
    private ErrorCode errorCode;

    private String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FieldErrorDto> errors;

    public static SimpleErrorResponse fail(ErrorCode errorCode){
       return  SimpleErrorResponse.builder()
               .errorCode(errorCode)
               .errorMessage(errorCode.getDescription())
               .build();
    }

    public static SimpleErrorResponse fail(ErrorCode errorCode, List<FieldErrorDto> fieldErrors) {
        return SimpleErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getDescription())
                .errors(fieldErrors)
                .build();
    }
}
