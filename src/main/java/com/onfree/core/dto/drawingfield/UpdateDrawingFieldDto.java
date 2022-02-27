package com.onfree.core.dto.drawingfield;

import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
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

    @ApiModelProperty(value = "상태 설정", example = "false", notes = "true 일 경우 보여지지 않음", allowableValues = "TEMP, TOP, USED")
    @NotNull(message = "표시 설정은 필수입니다.")
    private DrawingFieldStatus status;

    public DrawingField toEntity(){
        return DrawingField.builder()
                .fieldName(fieldName)
                .description(description)
                .status(status)
                .build();
    }
}
