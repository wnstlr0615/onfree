package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DrawingFieldErrorCode implements ErrorCode{
    NOT_FOUND_DRAWING_FIELD("해당 그림분야를 찾을 수 없습니다.",404),
    DUPLICATED_DRAWING_FIELD_NAME("해당 그림 분야명은 이미 등록되어 있습니다.",400),
    DRAWING_FIELD_EMPTY("등록된 그림 분야 명이 없습니다.", 400)
    ;
    private final String description;
    private final int status;
}
