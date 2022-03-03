package com.onfree.controller.admin;

import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.drawingfield.CreateDrawingFieldDto;
import com.onfree.core.dto.drawingfield.DrawingFieldDto;
import com.onfree.core.dto.drawingfield.UpdateDrawingFieldDto;
import com.onfree.core.service.DrawingFieldService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/api/v1/drawing-fields",  consumes = MediaType.APPLICATION_JSON_VALUE)
public class DrawingFieldAdminController {
    private final DrawingFieldService drawingFieldService;

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 상세 조회", notes = "그림 분야 상세 정보 조회 API")
    @GetMapping("/{drawingFieldId}")
    public DrawingFieldDto drawingFieldDetails(
            @PathVariable("drawingFieldId") Long drawingFieldId
    ){
        final DrawingFieldDto response = drawingFieldService.findDrawField(drawingFieldId);
        //링크 추가
        response.add(
                linkTo(methodOn(DrawingFieldAdminController.class).drawingFieldDetails(drawingFieldId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/drawing-field-admin-controller/drawingFieldDetailsUsingGET").withRel("profile")
        );
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 전체 조회", notes = "그림 분야 전체 조회 API")
    @GetMapping("")
    public CollectionModel<DrawingFieldDto> drawingFieldList(){

        CollectionModel<DrawingFieldDto> response = PagedModel.of(
                drawingFieldService.findAllDrawingField()
        );
        //링크 추가

        response.add(
                linkTo(DrawingFieldAdminController.class).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) +"/#/drawing-field-admin-controller/drawingFieldListUsingGET").withRel("profile")
        );
        response.forEach(
                drawingFieldDto -> drawingFieldDto.add(
                            linkTo(methodOn(DrawingFieldAdminController.class)
                                .drawingFieldDetails(drawingFieldDto.getDrawingFieldId())
                            ).withSelfRel()
                        )
        );
        return response;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 추가", notes = "그림 분야 추가 API")
    @PostMapping
    public ResponseEntity drawingFieldAdd(
            @Valid @RequestBody CreateDrawingFieldDto createDrawingFieldDto,
            BindingResult errors
    ){
        DrawingFieldDto response = drawingFieldService.addDrawingField(createDrawingFieldDto);
        //링크 추가

        response.add(
                linkTo(DrawingFieldAdminController.class).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/drawing-field-admin-controller/drawingFieldAddUsingPOST").withRel("profile"),
                linkTo(DrawingFieldAdminController.class).slash(response.getDrawingFieldId()).withRel("drawingField-details")
        );
        return ResponseEntity.created(
                linkTo(
                        methodOn(DrawingFieldAdminController.class)
                                .drawingFieldDetails(response.getDrawingFieldId())
                ).toUri()
        ).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "그림분야 수정", notes = "그림 분야 수정 API")
    @PutMapping("/{drawingFieldId}")
    public SimpleResponse drawingFieldModify(
            @ApiParam(value = "그림분야 PK", example = "1")
            @PathVariable("drawingFieldId") Long drawingFieldId,
            @Valid @RequestBody UpdateDrawingFieldDto updateDrawingFieldDto,
            BindingResult errors
    ){
        drawingFieldService.updateDrawingField(drawingFieldId, updateDrawingFieldDto);
        SimpleResponse response = SimpleResponse.success("성공적으로 그림 분야 수정이 완료되었습니다.");

        //링크 추가
        response.add(
                linkTo(DrawingFieldAdminController.class).slash(drawingFieldId).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/drawing-field-admin-controller/drawingFieldModifyUsingPUT").withRel("profile")
        );

        return response;
    }

    @DeleteMapping("/{drawingFieldId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SimpleResponse drawingFieldRemove(
            @ApiParam(value = "그림분야 PK", example = "1")
            @PathVariable("drawingFieldId") Long drawingFieldId
    ){
        drawingFieldService.removeDrawingField(drawingFieldId);
        SimpleResponse response = SimpleResponse.success("해당 그림분야를 성공적으로 삭제하엿습니다.");

        //링크 추가
        response.add(
                linkTo(DrawingFieldAdminController.class).slash(drawingFieldId).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/drawing-field-admin-controller/drawingFieldRemoveUsingDELETE").withRel("profile")
        );
        return response;
    }

}
