package com.onfree.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GlobalErrorCode implements ErrorCode {
    UNAUTHORIZED_ERROR("로그인 하지 않은 유저 입니다.", 401);
    private final String description;
    private final int status;
}
