package com.onfree.core.entity;

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

    @Column(nullable = false)
    private Boolean disabled;

    @Column(nullable = false)
    private Boolean top;

    public static DrawingField createDrawingField(String fieldName, String description, boolean top) {
        return DrawingField.builder()
                .fieldName(fieldName)
                .description(description)
                .disabled(false)
                .top(top)
                .build();
    }

    public void modifiedDrawingField(DrawingField drawingField) {
        this.fieldName =drawingField.getFieldName();
        this.description =drawingField.getDescription();
        this.disabled =drawingField.getDisabled();
        this.top =drawingField.getTop();
    }
}
