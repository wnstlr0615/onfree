package com.onfree.controller.admin;

import com.onfree.anotation.WithAdminUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.DrawingFieldErrorCode;
import com.onfree.common.error.exception.DrawingFieldException;
import com.onfree.core.dto.drawingfield.CreateDrawingFieldDto;
import com.onfree.core.dto.drawingfield.DrawingFieldDto;
import com.onfree.core.dto.drawingfield.UpdateDrawingFieldDto;
import com.onfree.core.service.DrawingFieldService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = DrawingFieldController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class DrawingFieldControllerTest extends ControllerBaseTest {
    @MockBean
    DrawingFieldService drawingFieldService;

    @Test
    @WithAdminUser
    @DisplayName("[성공][GET] 그림분야 상세 조회 ")
    public void givenDrawingFieldId_whenGetOneDrawingField_thenDrawingFieldDto() throws Exception{
        //given
        final long givenDrawingFieldId = 1L;
        final String name = "캐릭터 디자인";
        final String description = "캐릭터 디자인 그림";
        when(drawingFieldService.getOneDrawField(eq(givenDrawingFieldId)))
                .thenReturn(
                        getDrawingFieldDto(name, description)
                );
        //when
        //then
        mvc.perform(get("/admin/api/drawing-fields/{drawingFieldId}", givenDrawingFieldId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fieldName").value(name))
            .andExpect(jsonPath("$.description").value(description))
            .andExpect(jsonPath("$.top").value(false))
            .andExpect(jsonPath("$.disabled").value(false))
        ;
        verify(drawingFieldService).getOneDrawField(eq(givenDrawingFieldId));
    }

    private DrawingFieldDto getDrawingFieldDto(String fieldName, String description) {
        return DrawingFieldDto.builder()
                .fieldName(fieldName)
                .description(description)
                .top(false)
                .disabled(false)
                .build();
    }

    @Test
    @WithAdminUser
    @DisplayName("[실패][GET] 그림분야 상세 조회 - 해당 그림분야가 존재하지 않는 경우")
    public void givenWrongDrawingFieldId_whenGetOneDrawingField_thenNotFoundDrawingFieldError() throws Exception{
        //given
        final long givenWrongDrawingFieldId = 999L;
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD;
        when(drawingFieldService.getOneDrawField(eq(givenWrongDrawingFieldId)))
                .thenThrow(
                        new DrawingFieldException(errorCode)
                );
        //when
        //then
        mvc.perform(get("/admin/api/drawing-fields/{drawingFieldId}", givenWrongDrawingFieldId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(drawingFieldService).getOneDrawField(eq(givenWrongDrawingFieldId));
    }

    @Test
    @WithAdminUser
    @DisplayName("[성공][GET] 그림분야 전체 조회 ")
    public void givenNothing_whenGetDrawFieldList_thenDrawFieldList() throws Exception{
        //given
        when(drawingFieldService.getDrawFieldDtoList())
                .thenReturn(
                        getDrawingFieldDtoList()
                );
        //when
        //then
        mvc.perform(get("/admin/api/drawing-fields"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0]").isNotEmpty())
            .andExpect(jsonPath("$.[0].fieldName").value("캐릭터 디자인"))
            .andExpect(jsonPath("$.[1].fieldName").value("버츄얼 디자인"))
            .andExpect(jsonPath("$.[2].fieldName").value("일러스트"))
        ;
        verify(drawingFieldService).getDrawFieldDtoList();
    }

    private List<DrawingFieldDto> getDrawingFieldDtoList() {
        return List.of(
                getDrawingFieldDto("캐릭터 디자인" ),
                getDrawingFieldDto("버츄얼 디자인" ),
                getDrawingFieldDto("일러스트" )
        );
    }
    private DrawingFieldDto getDrawingFieldDto(String fieldName) {
        return DrawingFieldDto.builder()
                .fieldName(fieldName)
                .description(fieldName)
                .top(false)
                .disabled(false)
                .build();
    }

    @Test
    @WithAdminUser
    @DisplayName("[실패][GET] 그림분야 전체 조회 - 그림분야 리스트가 비어 있는 경우")
    public void givenNothing_whenGetEmptyDrawFieldList_thenEmptyDrawFieldList() throws Exception{
        //given
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.DRAWING_FIELD_EMPTY;

        when(drawingFieldService.getDrawFieldDtoList())
                .thenThrow(
                        new DrawingFieldException(errorCode)
                );
        //when
        //then
        mvc.perform(get("/admin/api/drawing-fields"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(drawingFieldService).getDrawFieldDtoList();

    }

    @Test
    @WithAdminUser
    @DisplayName("[성공][POST] 그림분야 등록 ")
    public void givenCreateDrawingFieldDto_thenCrateDrawingField_thenSimpleResponseSuccess() throws Exception{
        //given
        final CreateDrawingFieldDto createDrawingFieldDto = givenCreateDrawingFieldDto("캐릭터 디자인", "캐릭터 디자인");
        doNothing().when(drawingFieldService)
                .createDrawingField(any(CreateDrawingFieldDto.class));
        //when //then
        mvc.perform(post("/admin/api/drawing-fields")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createDrawingFieldDto
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("성공적으로 그림 분야를 추가하였습니다."))
        ;
        verify(drawingFieldService).createDrawingField(any(CreateDrawingFieldDto.class));
    }

    private CreateDrawingFieldDto givenCreateDrawingFieldDto(String fieldName, String description) {
        return CreateDrawingFieldDto.builder()
                .fieldName(fieldName)
                .description(description)
                .build();
    }

    @Test
    @WithAdminUser
    @DisplayName("[실패[POST] 그림분야 등록 - 이미 등록된 필드 명이 있을 경우")
    public void givenDuplicatedDrawingFieldDto_thenCrateDrawingField_thenDuplicatedDrawingFieldNameError() throws Exception{
        //given
        final CreateDrawingFieldDto createDrawingFieldDto = givenCreateDrawingFieldDto("캐릭터 디자인", "캐릭터 디자인");
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.DUPLICATED_DRAWING_FIELD_NAME;
        doThrow(new DrawingFieldException(errorCode))
                .when(drawingFieldService).createDrawingField(any(CreateDrawingFieldDto.class));

        //when //then
        mvc.perform(post("/admin/api/drawing-fields")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createDrawingFieldDto
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(drawingFieldService).createDrawingField(any(CreateDrawingFieldDto.class));

    }

    @Test
    @WithAdminUser
    @DisplayName("[성공][PUT] 작가 분야 수정")
    public void givenUpdateDrawingFieldDto_whenUpdateDrawingField_thenSimpleMessage() throws Exception{
        //given
        final long givenDrawingFieldId = 1L;
        doNothing().when(drawingFieldService)
                .updateDrawingField(eq(givenDrawingFieldId), any(UpdateDrawingFieldDto.class));
        //when
        //then
        mvc.perform(put("/admin/api/drawing-fields/{drawingFieldId}", givenDrawingFieldId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateDrawingFieldDto(
                                        "캐릭터 디자인", "캐릭터 디자인", true, true
                                )
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("성공적으로 그림 분야 수정이 완료되었습니다."))
        ;
        verify(drawingFieldService).updateDrawingField(eq(givenDrawingFieldId), any(UpdateDrawingFieldDto.class));
    }

    private UpdateDrawingFieldDto givenUpdateDrawingFieldDto(String fieldName, String description, boolean disabled, boolean top) {
        return UpdateDrawingFieldDto.builder()
                .fieldName(fieldName)
                .description(description)
                .disabled(disabled)
                .top(top)
                .build();
    }

    @Test
    @WithAdminUser
    @DisplayName("[실패][PUT] 작가 분야 수정 - 잘못된 필드 ID인 경우")
    public void givenWrongDrawingFieldId_whenUpdateDrawingField_thenNotFoundDrawingFieldError() throws Exception{
        //given
        final long givenWrongDrawingFieldId = 999L;
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD;
        doThrow(new DrawingFieldException(errorCode)).when(drawingFieldService)
                .updateDrawingField(eq(givenWrongDrawingFieldId), any(UpdateDrawingFieldDto.class));
        //when
        //then
        mvc.perform(put("/admin/api/drawing-fields/{drawingFieldId}", givenWrongDrawingFieldId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateDrawingFieldDto(
                                        "캐릭터 디자인", "캐릭터 디자인", true, true
                                )
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(drawingFieldService).updateDrawingField(eq(givenWrongDrawingFieldId), any(UpdateDrawingFieldDto.class));
    }

    @Test
    @WithAdminUser
    @DisplayName("[성공][DELETE] 작가 분야 삭제")
    public void givenDeleteDrawingFieldId_whenDeleteDrawingField_thenSimpleMessage() throws Exception{
        //given
        final long givenDrawingFieldId = 1L;
        doNothing().when(drawingFieldService)
                .deleteDrawingField(eq(givenDrawingFieldId));
        //when
        //then
        mvc.perform(delete("/admin/api/drawing-fields/{drawingFieldId}", givenDrawingFieldId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("해당 그림분야를 성공적으로 삭제하엿습니다."))
        ;
        verify(drawingFieldService).deleteDrawingField(eq(givenDrawingFieldId));
    }

    @Test
    @WithAdminUser
    @DisplayName("[실패][DELETE] 작가 분야 삭제 - 해당 작가분야가 없는 경우")
    public void givenDeleteDrawingFieldId_whenDeleteDrawingField_thenNotFoundDrawingFieldError() throws Exception{
        //given
        final long givenWrongDrawingFieldId = 999L;
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD;
        doThrow(new DrawingFieldException(errorCode)).when(drawingFieldService)
                .deleteDrawingField(eq(givenWrongDrawingFieldId));
        //when
        //then
        mvc.perform(delete("/admin/api/drawing-fields/{drawingFieldId}", givenWrongDrawingFieldId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(drawingFieldService).deleteDrawingField(eq(givenWrongDrawingFieldId));
    }
}