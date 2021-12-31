package com.onfree.error.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
@Builder
public class FieldErrorDto {
    private final String field;
    private final String message;

     public static FieldErrorDto fromFieldError(FieldError fieldError){
         return FieldErrorDto.builder()
                 .field(fieldError.getField())
                 .message(fieldError.getDefaultMessage())
                 .build();
     }
}
