package com.onfree.core.entity.portfolio;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PortfolioStatus {
    NORMAL("정상적인 포트폴리오"),
    REPRESENTATION("대표 포트폴리오"),
    TEMPORARY("임시 저장된 포트폴리오"),
    DELETED("삭제된 포트폴리오"),
    HIDDEN("숨겨진 포트폴리오")
    ;
    private final String description;
}
