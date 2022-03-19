package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestApplyErrorCode implements ErrorCode{
    NOT_FOUND_REQUEST_APPLY_ID("해당 의뢰 요청 아이디를 찾을 수 없습니다.", 400),

    CAN_NOT_APPLY_REAL_TIME_REQUEST_DELETED("해당 의뢰는 삭제되어 지원이 불가합니다.", 400),
    CAN_NOT_APPLY_REAL_TIME_REQUEST_FINISHED("해당 의뢰는 마감되 어 지원이 불가합니다.", 400),
    CAN_NOT_APPLY_REAL_TIME_REQUEST_REQUESTING("현재 의뢰 중이므로 지원이 불가합니다.", 400),

    CAN_NOT_SENT_QUOTATION("이미 견적서(제안서)를 수락하여 견적서 전송이 불가합니다.", 400),

    ALREADY_ACCEPT("이미 견적서(제안서)를 수락 하였습니다.", 400),
    DO_NOT_HAVE_RECEIVED_ESTIMATE("아직 견적서(제안서)를 받지 못했습니다.", 400)
    ;
    private final String description;
    private final int status;
}
