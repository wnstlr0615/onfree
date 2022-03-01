package com.onfree.core.dto.drawingfield.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDrawingFieldsDto {
    @NotNull(message = "drawingFields 항목은 null 일 수 없습니다.")
    List<Long> drawingFields;
}
