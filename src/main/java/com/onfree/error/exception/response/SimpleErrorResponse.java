package com.onfree.error.exception.response;

import com.onfree.error.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleErrorResponse {
    private ErrorCode errorCode;
    private String errorMessage;

    public static SimpleErrorResponse fail(ErrorCode errorCode){
       return  SimpleErrorResponse.builder()
               .errorCode(errorCode)
               .errorMessage(errorCode.getDescription())
               .build();
    }
}
