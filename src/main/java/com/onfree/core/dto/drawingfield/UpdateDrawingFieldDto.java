package com.onfree.core.dto.drawingfield;

import com.onfree.core.entity.DrawingField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDrawingFieldDto {
    @ApiModelProperty(value = "그림분야 명", example = "캐릭터 디자인")
    @NotBlank(message = "필드명은 필수입니다.")
    private String fieldName;

    @ApiModelProperty(value = "그림분야 설명", example = "캐릭터 관련 디자인 ")
    @NotBlank(message = "필드명은 필수입니다.")
    private String description;

    @ApiModelProperty(value = "숨김 설정", example = "false", notes = "true 일 경우 보여지지 않음", allowableValues = "true,false")
    @NotNull(message = "표시 설정은 필수입니다.")
    private Boolean disabled;

    @ApiModelProperty(value = "상단 고정 설정", example = "false", notes = "true 일 경우 다른 그림분야보다 상단에 노출", allowableValues = "true,false")
    @NotNull(message = "상단 고정 설정은 필수입니다.")
    private Boolean top;

    public DrawingField toEntity(){
        return DrawingField.builder()
                .fieldName(fieldName)
                .description(description)
                .disabled(disabled)
                .top(top)
                .build();
    }
}
