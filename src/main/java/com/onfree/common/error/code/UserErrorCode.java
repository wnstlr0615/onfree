package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode{
    USER_EMAIL_DUPLICATED("해당 이메일은 사용중입니다.", 400),
    NOT_FOUND_USER_EMAIL("해당 이메일을 가지는 회원이 없습니다", 400),
    NOT_FOUND_USERID("해당 유저아이디는 없는 회원입니다.", 404),
    ALREADY_USER_DELETED("해당 아이디는 이미 삭제 처리 되었습니다. ", 400),
    USER_NICKNAME_DUPLICATED("해당 닉네임은 이미 사용 중 입니다.", 400)
    ;

    private final String description;
    private final int status;
}
