package com.onfree.core.dto.drawingfield;

import com.onfree.core.entity.DrawingField;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Builder
@Relation(collectionRelation = "items")
public class DrawingFieldDto extends RepresentationModel<DrawingFieldDto> {
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
