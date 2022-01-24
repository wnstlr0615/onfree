package com.onfree.controller.admin;

import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.drawingfield.CreateDrawingFieldDto;
import com.onfree.core.dto.drawingfield.DrawingFieldDto;
import com.onfree.core.dto.drawingfield.UpdateDrawingFieldDto;
import com.onfree.core.service.DrawingFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "그림분야 설정", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/admin/api/drawing-fields")
public class DrawingFieldController {
    private final DrawingFieldService drawingFieldService;



    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 상세 조회", notes = "그림 분야 상세 정보 조회 API")
    @GetMapping("/{drawingFieldId}")
    public DrawingFieldDto getOneDrawFieldDto(
            @PathVariable("drawingFieldId") Long drawingFieldId
    ){
        return drawingFieldService.getOneDrawField(drawingFieldId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 전체 조회", notes = "그림 분야 전체 조회 API")
    @GetMapping("")
    public List<DrawingFieldDto> getDrawFieldList(){
        return drawingFieldService.getDrawFieldDtoList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 추가", notes = "그림 분야 추가 API")
    @PostMapping
    public SimpleResponse createDrawingField(
            @Valid @RequestBody CreateDrawingFieldDto createDrawingFieldDto,
            BindingResult errors){
            drawingFieldService.createDrawingField(createDrawingFieldDto);
            return SimpleResponse.success("성공적으로 그림 분야를 추가하였습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 수정", notes = "그림 분야 수정 API")
    @PutMapping("/{drawingFieldId}")
    public SimpleResponse updateDrawingField(
            @ApiParam(value = "그림분야 PK", example = "1")
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
            @ApiParam(value = "그림분야 PK", example = "1")
            @PathVariable("drawingFieldId") Long drawingFieldId
    ){
        drawingFieldService.deleteDrawingField(drawingFieldId);
        return SimpleResponse.success("해당 그림분야를 성공적으로 삭제하엿습니다.");
    }

}
