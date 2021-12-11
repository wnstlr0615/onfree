package com.onfree.error.exception.response;

import com.onfree.error.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class ResponseResult {
    public static ResponseEntity<?> fail(ErrorCode code){
        return ResponseEntity.status(
                code.getStatus()
        ).body(
                SimpleErrorResponse.fail(code)
        );
    }

}
