package com.onfree.core.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class LoginFormDto {
    @ApiModelProperty(value = "사용자 id(email)", example = "jun@naver.com")
    private String email;

    @ApiModelProperty(value = "사용자 패스워드", example = "!abcdefghijk123")
    private String password;
}
