package com.onfree.core.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Builder
@Getter
public class UpdatePasswordDto {
    @NotBlank(message = "인증용 uuid는 필수 입니다.")
    @ApiModelProperty(value = "인증용 uuid", example = "79b2c257-3146-4d01-bcf0-e90477559969")
    private final String uuid;

    @NotBlank(message = "새로운 비밀번호 입력은 필 수 입니다.")
    @ApiModelProperty(value = "새로운 패스워드", example = "newPassword")
    private final String newPassword;
}
