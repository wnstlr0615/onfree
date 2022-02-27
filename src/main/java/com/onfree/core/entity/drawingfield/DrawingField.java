package com.onfree.core.entity.drawingfield;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DrawingField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drawingFieldId;

    @Column(nullable = false)
    private String fieldName;

    private String description;

    @Enumerated(EnumType.STRING)
    private DrawingFieldStatus status;

    //== 생성 메서드 ==//
    public static DrawingField createDrawingField(String fieldName, String description, DrawingFieldStatus status) {
        return DrawingField.builder()
                .fieldName(fieldName)
                .description(description)
                .status(status)
                .build();
    }

    public void updateDrawingField(DrawingField drawingField) {
        this.fieldName =drawingField.getFieldName();
        this.description =drawingField.getDescription();
        this.status =drawingField.getStatus();
    }

    public void delete() {
        this.status = DrawingFieldStatus.DISABLED;
    }
}
