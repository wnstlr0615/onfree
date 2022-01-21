package com.onfree.controller.admin;

import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.drawingfield.CreateDrawingFieldDto;
import com.onfree.core.dto.drawingfield.DrawingFieldDto;
import com.onfree.core.dto.drawingfield.UpdateDrawingFieldDto;
import com.onfree.core.service.DrawingFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/api/drawing-fields")
public class DrawingFieldController {
    private final DrawingFieldService drawingFieldService;


    @GetMapping("/{drawingFieldId}")
    @PreAuthorize("hasRole('ADMIN')")
    public DrawingFieldDto getOneDrawFieldDto(
            @PathVariable("drawingFieldId") Long drawingFieldId
    ){
        return drawingFieldService.getOneDrawField(drawingFieldId);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DrawingFieldDto> getDrawFieldList(){
        return drawingFieldService.getDrawFieldDtoList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SimpleResponse createDrawingField(
            @Valid @RequestBody CreateDrawingFieldDto createDrawingFieldDto,
            BindingResult errors){
            drawingFieldService.createDrawingField(createDrawingFieldDto);
            return SimpleResponse.success("성공적으로 그림 분야를 추가하였습니다.");
    }

    @PutMapping("/{drawingFieldId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SimpleResponse updateDrawingField(
            @PathVariable("drawingFieldId") Long drawingFieldId,
            @Valid @RequestBody UpdateDrawingFieldDto updateDrawingFieldDto,
            BindingResult errors
    ){
        drawingFieldService.updateDrawingField(drawingFieldId, updateDrawingFieldDto);
        return SimpleResponse.success("성공적으로 그림 분야 수정이 완료되었습니다.");
    }

    @DeleteMapping("/{drawingFieldId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SimpleResponse deleteDrawingField(
            @PathVariable("drawingFieldId") Long drawingFieldId
    ){
        drawingFieldService.deleteDrawingField(drawingFieldId);
        return SimpleResponse.success("해당 그림분야를 성공적으로 삭제하엿습니다.");
    }

}
