package com.onfree.core.dto.user.artist.status;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusMarkDto {
    @ApiModelProperty(name = "영업 마크", example = "OPEN", allowableValues = "OPEN,CLOSE,REST")
    @NotNull(message = "영업마크를 지정해주세요 ")
    private String statusMark;
}
