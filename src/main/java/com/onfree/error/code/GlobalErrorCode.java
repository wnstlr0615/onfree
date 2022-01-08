package com.onfree.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GlobalErrorCode implements ErrorCode {
    UNAUTHORIZED_ERROR("로그인 하지 않은 유저 입니다.", 401),
    NOT_VALIDATED_REQUEST_BODY("요청 정보가 올바르지 않습니다.", 400),
    ACCESS_DENIED("접근 권한이 없습니다.", 403)
    ;
    private final String description;
    private final int status;


}
