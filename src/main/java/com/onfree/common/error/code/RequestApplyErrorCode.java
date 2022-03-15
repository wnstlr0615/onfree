package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestApplyErrorCode implements ErrorCode{
    CAN_NOT_APPLY_REAL_TIME_REQUEST_DELETED("해당 의뢰는 삭제되어 지원이 불가합니다.", 400),
    CAN_NOT_APPLY_REAL_TIME_REQUEST_FINISHED("해당 의뢰는 마감되 어 지원이 불가합니다.", 400),
    CAN_NOT_APPLY_REAL_TIME_REQUEST_REQUESTING("현재 의뢰 중이므로 지원이 불가합니다.", 400)
    ;
    private final String description;
    private final int status;
}
