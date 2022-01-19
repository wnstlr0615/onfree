package com.onfree.common.error.response;

import com.onfree.common.error.code.ErrorCode;
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
