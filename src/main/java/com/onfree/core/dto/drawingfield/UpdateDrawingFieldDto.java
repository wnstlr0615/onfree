package com.onfree.core.dto.drawingfield;

import com.onfree.core.entity.DrawingField;
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
    @NotBlank(message = "필드명은 필수입니다.")
    private String fieldName;

    @NotBlank(message = "필드명은 필수입니다.")
    private String description;

    @NotNull(message = "표시 설정은 필수입니다.")
    private Boolean disabled;

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
