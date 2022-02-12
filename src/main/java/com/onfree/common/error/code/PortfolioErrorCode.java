package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PortfolioErrorCode implements ErrorCode{
    NOT_FOUND_PORTFOLIO("해당 포트폴리오를 찾을 수 없습니다.", 404),
    ALREADY_DELETED_PORTFOLIO("해당 포트폴리오를 이미 삭제하였습니다.", 400),
    NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO("임시 저장 포트폴리오는 대표 포트폴리오로 지정할 수 업습니다.", 400),
    NOT_ACCESS_PORTFOLIO("해당 포트폴리오에 대한 접근 권한이 없습니다.", 400),
    ALREADY_REGISTER_REPRESENTATIVE_PORTFOLIO("해당 포트폴리오는 이미 대표 포트폴리오로 등록되었습니다. ", 200)
    ;
    private final String description;
    private final int status;
}
