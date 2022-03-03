package com.onfree.controller;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.drawingfield.artist.UpdateDrawingFieldsDto;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.ArtistUserDrawingFieldService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users/artist", consumes = MediaType.APPLICATION_JSON_VALUE)
public class ArtistUserDrawingFieldController {
    private final ArtistUserDrawingFieldService drawingFieldService;

    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "그림분야 변경")
    @PutMapping("/me/drawing-fields")
    public SimpleResponse updateDrawingFields(
            @CurrentArtistUser ArtistUser artistUser,
            @Valid @RequestBody UpdateDrawingFieldsDto updateDrawingFieldsDto
    ){
        drawingFieldService.updateDrawingFields(artistUser.getUserId(), updateDrawingFieldsDto);
        return SimpleResponse.success("그림분야가 성공적으로 등록되었습니다.");
    }

    @ApiOperation(value = "작가유저 그림 분야 목록 조회")
    @GetMapping("/{userId}/drawing-fields")
    public List<UsedDrawingFieldDto> getAllArtistUserDrawingFields(
            @PathVariable("userId") Long userId
    ){
        return drawingFieldService.getAllArtistUserUsedDrawingFields(userId);
    }

}
