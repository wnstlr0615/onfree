package com.onfree.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode{
    USER_EMAIL_DUPLICATED("해당 이메일은 사용중입니다.", 200),
    NOT_VALID_REQUEST_PARAMETERS("잘못된 파라미터 요청입니다.", 200);

    private String description;
    private int status;
}
