package com.onfree.core.entity.fileitem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileType {
    PROFILE_IMAGE_FILE("프로필 이미지 파일", "/users/profile-image"),
    PORTFOLIO_MAIN_IMAGE("포트폴리오 메인 이미지", "/portfolio/main-image"),
    PORTFOLIO_CONTENT_IMAGE("포트폴리오 본문 이미지", "/portfolio/content-image"),
    REQUEST_REFERENCE_FILE("의뢰 프로젝트 참고 파일", "/users/request/reference-file")
    ;
    private final String description;
    private final String path;
}
