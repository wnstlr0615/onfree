package com.onfree.common.error.code;

import com.onfree.common.error.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginErrorCode implements ErrorCode {
    EMAIL_OR_PASSWORD_WRONG("이메일 또는 패스워드가 잘못입력되었습니다.", 200),
    WRONG_LOGIN_FORM("로그인 요청이 올바르지 않습니다.", 400),
    TOKEN_IS_EXPIRED("토큰이 만료되었습니다. 재로그인 바랍니다.", 200),
    INPUT_WRONG_TOKEN("잘못된 토큰이 입력되었습니다.", 400),
    NOT_FOUND_REFRESH_TOKEN("Refresh_Token 이 없습니다. 재로그인 바랍니다", 401)
    ;
    private final String description;
    private final int status;
}
