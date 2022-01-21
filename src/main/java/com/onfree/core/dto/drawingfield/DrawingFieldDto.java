package com.onfree.core.dto.drawingfield;

import com.onfree.core.entity.DrawingField;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrawingFieldDto {
    private Long drawingFieldId;
    private String fieldName;
    private String description;
    private Boolean disabled;
    private Boolean top;

    public static DrawingFieldDto fromEntity(DrawingField drawingField){
        return DrawingFieldDto.builder()
                .drawingFieldId(drawingField.getDrawingFieldId())
                .fieldName(drawingField.getFieldName())
                .description(drawingField.getDescription())
                .disabled(drawingField.getDisabled())
                .top(drawingField.getTop())
                .build();
    }
}
