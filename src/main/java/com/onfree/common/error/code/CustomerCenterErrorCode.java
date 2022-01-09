package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerCenterErrorCode implements ErrorCode{
    NOTICES_IS_EMPTY("등록된 공지가 없습니다.", 200),
    NOT_FOUND_NOTICE("해당 공지를 찾을 수 없습니다.", 404),

    QUESTION_IS_EMPTY("등록된 자주하기 질문이 없습니다.", 200),
    NOT_FOUND_QUESTION("해당 자주하는 질문을 찾을 수 없습니다.", 404),
    ;

    private final String description;
    private final int status;
}
