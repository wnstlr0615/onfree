package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RealTimeRequestErrorCode implements ErrorCode{
    NOT_FOUND_REAL_TIME_REQUEST("해당 실시간 의뢰를 찾을 수 없습니다.", 404),
    REAL_TIME_REQUEST_ALREADY_DELETED("해당 실시간 의뢰는 이미 삭제되었습니다.", 400),
    UPDATE_START_TIME_MUST_BE_AFTER_CREATE_DATE("새로운 시작 시간은 생성 시간보다 과거일 수 없습니다.", 400),
    REAL_TIME_REQUEST_DELETED("해당 실시간 의뢰는 삭제되었습니다.", 400),
    FINISH_REQUEST_CAN_NOT_UPDATE("마감 된 실시간 의뢰는 수정을 할 수 없습니다.", 400),
    REAL_TIME_REQUEST_STATUS_ALREADY_FINISH("해당 실시간 의뢰는 이미 마감 되었습니다.", 400),
            ;
    private final String description;
    private final int status;
}
