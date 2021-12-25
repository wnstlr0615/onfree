package com.onfree.error.exception.response;

import com.onfree.error.code.ErrorCode;
import com.onfree.error.exception.FieldErrorDto;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class ResponseResult {
    public static ResponseEntity<?> fail(ErrorCode code){
        return ResponseEntity.status(
                code.getStatus()
        ).body(
                SimpleErrorResponse.fail(code)
        );
    }
    public static ResponseEntity<?> fail(ErrorCode code, List<FieldErrorDto> fieldErrors){
        return ResponseEntity.status(
                code.getStatus()
        ).body(
                SimpleErrorResponse.fail(code, fieldErrors)
        );
    }

}
