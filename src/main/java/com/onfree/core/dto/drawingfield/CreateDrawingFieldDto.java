package com.onfree.core.dto.drawingfield;

import com.onfree.core.entity.DrawingField;
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
    @NotBlank(message = "필드이름은 필수 입니다.")
    private String fieldName;

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
