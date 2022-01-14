package com.onfree.core.entity.fileitem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileType {
    PROFILE_IMAGE_FILE("프로필 이미지 파일")
    ;
    private final String description;
}
