package com.onfree.core.dto.drawingfield.artist;

import com.onfree.core.entity.drawingfield.DrawingField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class UsedDrawingFieldDto {
    @ApiModelProperty(value = "그림분야 PK", notes = "그림분야 PK")
    private Long drawingFieldId;

    @ApiModelProperty(value = "그림분야 이름", notes = "그림분야 이름")
    private String drawingFieldName;

    @ApiModelProperty(value = "그림분야 체크 유무", notes = "그림분야 체크 유무")
    private Boolean used;

    //== 생성 메소드 ==//
    public static UsedDrawingFieldDto createUsedDrawingFieldDto(String drawingFieldName, boolean used) {
        return UsedDrawingFieldDto.builder()
                .drawingFieldName(drawingFieldName)
                .used(used)
                .build();
    }

    public static UsedDrawingFieldDto createUsedDrawingFieldDtoFromEntity(DrawingField drawingField, boolean used) {
        return UsedDrawingFieldDto.builder()
                .drawingFieldId(drawingField.getDrawingFieldId())
                .drawingFieldName(drawingField.getFieldName())
                .used(used)
                .build();
    }
}
