package com.onfree.core.dto.user.artist;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateNicknameDto {
    @NotBlank(message = "nickname은 공백일 수 없습니다.")
    @ApiModelProperty(value = "변경할 새로운 닉네임", example = "새로운 닉네임")
    private String nickname;
}
