package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GlobalErrorCode implements ErrorCode {
    UNAUTHORIZED_ERROR("로그인 하지 않은 유저 입니다.", 401),
    NOT_VALIDATED_REQUEST("요청 정보가 올바르지 않습니다.", 400),
    ACCESS_DENIED("접근 권한이 없습니다.", 403),
    FAIL_SEND_MAIL("메일 전송에 실패 하였습니다", 400),

    INTERNAL_SERVER_ERROR("서버에 문제가 발생하였습니다.", 500)
    ;
    private final String description;
    private final int status;


}
