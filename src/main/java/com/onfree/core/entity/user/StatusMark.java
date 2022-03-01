package com.onfree.core.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusMark {
    OPEN("영업 중"),
    CLOSE("마감"),
    REST("쉬는 중")
    ;
    private final String description;
}
