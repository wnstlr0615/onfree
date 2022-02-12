package com.onfree.controller;

import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.drawingfield.artist.UpdateDrawingFieldsDto;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.service.ArtistUserDrawingFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "그림분야 설정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/artist")
public class ArtistUserDrawingFieldController {
    private final ArtistUserDrawingFieldService drawingFieldService;

    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @ApiOperation(value = "그림분야 변경")
    @PutMapping("/{userId}/drawing-fields")
    public SimpleResponse<?> UpdateDrawingFields(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateDrawingFieldsDto updateDrawingFieldsDto
    ){
        drawingFieldService.updateDrawingFields(userId, updateDrawingFieldsDto);
        return SimpleResponse.success("그림분야가 성공적으로 등록되었습니다.");
    }

    @ApiOperation(value = "작가유저 그림 분야 목록 조회")
    @GetMapping("/{userId}/drawing-fields")
    public SimpleResponse<?> getAllArtistUserDrawingFields(
            @PathVariable("userId") Long userId
    ){
        final List<UsedDrawingFieldDto> artistUserUsedDrawingFields = drawingFieldService.getAllArtistUserUsedDrawingFields(userId);
        return SimpleResponse.success(null, artistUserUsedDrawingFields);
    }

}
