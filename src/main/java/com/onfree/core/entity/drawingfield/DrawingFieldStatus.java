package com.onfree.core.entity.drawingfield;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DrawingFieldStatus {
    USED("사용중인 상태"),
    DISABLED("사용중지 상태"),
    TEMP("임시 저장 상태"),
    TOP("상단 등록된 상태")
    ;
    private final String description;
}
