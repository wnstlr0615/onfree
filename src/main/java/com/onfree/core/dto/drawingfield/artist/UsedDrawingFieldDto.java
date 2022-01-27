package com.onfree.core.dto.drawingfield.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UsedDrawingFieldDto {
    private Long drawingFieldId;
    private String drawingFieldName;
    private Boolean used;
}
