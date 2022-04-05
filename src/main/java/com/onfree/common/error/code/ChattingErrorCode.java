package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChattingErrorCode implements ErrorCode{
    MUST_NOT_EQUALS_SENDER_RECIPIENT("송신자와 수신자가 같을 수 없습니다.", 400),
    WRONG_REQUEST_APPLY_ID("잘 못된 실시간의뢰 id를 입력하였습니다.",400)
    ;
    private final String description;
    private final int status;
}
