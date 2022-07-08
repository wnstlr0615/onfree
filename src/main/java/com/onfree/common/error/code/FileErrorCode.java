package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileErrorCode implements ErrorCode{
    NOT_FOUND_FILENAME("해당 파일을 찾을 수 없습니다.", 404),
    NOT_ALLOW_UPLOAD_FILETYPE("해당 파일 타입 업로드를 지원하지 않습니다.", 400),
    FILE_UPLOAD_ERROR("파일 업로드중 에러가 발생하였습니다", 500),
    LOAD_FILE_ERROR("파일을 읽어오는 중 에러가 발생하였습니다", 500),

            ;
    private final String description;
    private final int status;
}
