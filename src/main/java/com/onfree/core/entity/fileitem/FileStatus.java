package com.onfree.core.entity.fileitem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileStatus {
    TEMP("임시 저장 파일"),
    USED("현재 사용중인 파일"),
    ;
    private final  String description;
}
