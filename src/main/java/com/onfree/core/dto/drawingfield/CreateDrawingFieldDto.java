package com.onfree.core.dto.drawingfield;

import com.onfree.core.entity.DrawingField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDrawingFieldDto {
    @ApiModelProperty(value = "그림분야 명칭", example = "캐릭터 디자인")
    @NotBlank(message = "필드이름은 필수 입니다.")
    private String fieldName;

    @ApiModelProperty(value = "그림분야 설명", example = "캐릭터 관련 디자인 분야")
    @NotBlank(message = "설명은 필수 입니다.")
    private String description;
    public DrawingField toEntity(){
        return DrawingField.builder()
                .fieldName(fieldName)
                .description(description)
                .disabled(false)
                .top(false)
                .build();
    }
}
