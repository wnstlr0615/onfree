package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileErrorCode implements ErrorCode{
    NOT_FOUND_FILENAME("해당 파일을 찾을 수 없습니다.", 404),
    NOT_ALLOW_UPLOAD_FILETYPE("해당 파일 타입 업로드를 지원하지 않습니다.", 400)
    ;
    private final String description;
    private final int status;
}
