package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SignUpErrorCode implements ErrorCode{
    EMAIL_IS_DUPLICATED("해당 이메일은 사용중입니다.", 200),
    NICKNAME_IS_DUPLICATED("해당 닉네임은 사용중입니다.", 200),
    EXPIRED_EMAIL_VERIFICATION("이메일 인증 시간이 만료 되었습니다.", 400),
    PERSONAL_URL_IS_DUPLICATED(" 해당 URL 은 이미 사용 중입니다.", 200)
    ;

    private final String description;
    private final int status;
}
