package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.common.WebMvcBaseTest;
import com.onfree.core.dto.drawingfield.artist.UpdateDrawingFieldsDto;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.service.ArtistUserDrawingFieldService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistUserDrawingFieldController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ArtistUserDrawingFieldControllerTest extends WebMvcBaseTest {
    @MockBean
    ArtistUserDrawingFieldService artistUserDrawingFieldService;

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 그림분야 변경")
    public void givenUpdateDrawingFieldsDto_whenUpdateDrawingFields_then_SimpleSuccess() throws Exception{
        //given
        final long givenUserId = 1L;

        doNothing().when(artistUserDrawingFieldService)
                .updateDrawingFields(anyLong(), any(UpdateDrawingFieldsDto.class));
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when
        //then
        mvc.perform(put("/api/users/artist/{userId}/drawing-fields", givenUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            givenUpdateDrawingFieldsDto()
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("그림분야가 성공적으로 등록되었습니다."))
        ;
        verify(artistUserDrawingFieldService).updateDrawingFields(eq(givenUserId), any(UpdateDrawingFieldsDto.class));
    }

    private UpdateDrawingFieldsDto givenUpdateDrawingFieldsDto() {

        return UpdateDrawingFieldsDto.builder()
                .drawingFields(
                        List.of(1L, 2L, 3L)
                )
                .build();
    }
    @Test
    @DisplayName("[성공][GET] 작가유저 그림 분야 목록 조회")
    public void givenUserId_whenGetAllArtistUserDrawingFields_thenSuccessArtistUserDrawingFieldDtoList() throws Exception{
        //given
        final long givenUserId = 1L;
        when(artistUserDrawingFieldService.getAllArtistUserUsedDrawingFields(anyLong()))
                .thenReturn(getUsedDrawingFieldDtoList());
        //when
        //then
        mvc.perform(get("/api/users/artist/{userId}/drawing-fields", givenUserId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.data[0].drawingFieldId").value(1))
            .andExpect(jsonPath("$.data[0].drawingFieldName").value("캐릭터 디자인"))
            .andExpect(jsonPath("$.data[0].used").value(true))
        ;
        verify(artistUserDrawingFieldService).getAllArtistUserUsedDrawingFields(eq(givenUserId));
    }

    private List<UsedDrawingFieldDto> getUsedDrawingFieldDtoList() {
        return List.of(
                createdUsedDrawingFieldDto(1L, "캐릭터 디자인", true),
                createdUsedDrawingFieldDto(2L, "메타버스", true),
                createdUsedDrawingFieldDto(3L, "일러스트", false),
                createdUsedDrawingFieldDto(4L, "게임삽화/원화", false)
        );
    }

    private UsedDrawingFieldDto createdUsedDrawingFieldDto(long drawingFieldId, String drawingFieldName, boolean used) {
        return UsedDrawingFieldDto.builder()
                .drawingFieldId(drawingFieldId)
                .drawingFieldName(drawingFieldName)
                .used(used)
                .build();
    }


}