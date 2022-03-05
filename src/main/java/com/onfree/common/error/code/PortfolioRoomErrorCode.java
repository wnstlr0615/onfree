package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PortfolioRoomErrorCode implements ErrorCode{
    NOT_FOUND_PORTFOLIO_ROOM("해당 포트폴리룸을을 찾을 수 없습니다.", 404),
    NOT_ACCESS_PORTFOLIO_ROOM("해당 포트폴리오룸은 접근 할 수 없습니다.", 400),
    PORTFOLIO_ROOM_IS_PRIVATE("해당 포트폴리오룸은 사용자에 의하여 비공개 처리 되었습니다.", 400),
    CAN_NOT_USE_PORTFOLIO_ROOM("해당 포트폴리오룸은 이용할 수 없습니다.", 400),
    ;
    private final String description;
    private final int status;
}
