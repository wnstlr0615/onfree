package com.onfree.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode{
    USER_EMAIL_DUPLICATED("해당 이메일은 사용중입니다.", 200),
    NOT_FOUND_USERID("해당 유저아이디는 없는 회원입니다.", 200),
    NOT_VALID_REQUEST_PARAMETERS("잘못된 파라미터 요청입니다.", 200);

    private final String description;
    private final int status;
}
