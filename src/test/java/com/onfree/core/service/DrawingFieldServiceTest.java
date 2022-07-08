package com.onfree.core.service;

import com.onfree.common.error.code.DrawingFieldErrorCode;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.exception.DrawingFieldException;
import com.onfree.core.dto.drawingfield.CreateDrawingFieldDto;
import com.onfree.core.dto.drawingfield.DrawingFieldDto;
import com.onfree.core.dto.drawingfield.UpdateDrawingFieldDto;
import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
import com.onfree.core.repository.DrawingFieldRepository;
import com.onfree.core.service.drawingfield.DrawingFieldService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrawingFieldServiceTest {
    @Mock
    DrawingFieldRepository drawingFieldRepository;

    @InjectMocks
    DrawingFieldService drawingFieldService;

    @Test
    @DisplayName("[성공] 그림 분야 전체 조회")
    public void givenNothing_whenGetDrawFieldDtoList_thenDrawingFieldDtoList(){
        //given
        when(drawingFieldRepository.findAllByStatusNot(eq(DrawingFieldStatus.DISABLED)))
            .thenReturn(
                    getDrawingFieldList()
            );
        //when
        final List<DrawingFieldDto> drawFieldDtoList = drawingFieldService.findAllDrawingField();

        //then
        assertAll(
                () ->         assertThat(drawFieldDtoList.size()).isEqualTo(3),
                () ->         assertThat(drawFieldDtoList.get(0).getFieldName()).isEqualTo("캐릭터 디자인"),
                () ->         assertThat(drawFieldDtoList.get(1).getFieldName()).isEqualTo("일러스트"),
                () ->         assertThat(drawFieldDtoList.get(1).getDescription()).isEqualTo("일러스트"),
                () ->         assertThat(drawFieldDtoList.get(1).getStatus()).isNotNull()
        );
        verify(drawingFieldRepository).findAllByStatusNot(eq(DrawingFieldStatus.DISABLED));
    }

    private List<DrawingField> getDrawingFieldList() {
        return List.of(
                getDrawingField("캐릭터 디자인"),
                getDrawingField("일러스트"),
                getDrawingField("버츄얼 디자인")
        );
    }

    private DrawingField getDrawingField(String fieldName) {
        return DrawingField.builder()
                .fieldName(fieldName)
                .description(fieldName)
                .status(DrawingFieldStatus.USED)
                .build();
    }

    @Test
    @DisplayName("[실패] 그림 분야 전체 조회 - 등록된 그림 분야가 없는 경우")
    public void givenNothing_whenGetEmptyDrawFieldDtoList_thenDrawingFieldEmptyError(){
        //given
        ErrorCode errorCode = DrawingFieldErrorCode.DRAWING_FIELD_EMPTY;
        when(drawingFieldRepository.findAllByStatusNot(eq(DrawingFieldStatus.DISABLED)))
                .thenReturn(
                        List.of()
                );
        //when

        //then
        final DrawingFieldException drawingFieldException = assertThrows(DrawingFieldException.class,
                () -> drawingFieldService.findAllDrawingField()
        );
        assertAll(
                () -> assertThat(drawingFieldException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(drawingFieldException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(drawingFieldRepository).findAllByStatusNot(eq(DrawingFieldStatus.DISABLED));
    }

    @Test
    @DisplayName("[성공] 그림 분야 상세 조회")
    public void givenDrawingFieldId_whenGetOneDrawingFieldDto_thenDrawingFieldDto(){
        //given
        final String fieldName = "캐릭터 디자인";
        when(drawingFieldRepository.findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED)))
                .thenReturn(
                        Optional.ofNullable(
                                getDrawingField(fieldName)
                        )
                );
        final long drawingFieldId = 1L;

        //when
        final DrawingFieldDto drawingFieldDto = drawingFieldService.findDrawField(drawingFieldId);

        //then
        assertAll(
                () ->         assertThat(drawingFieldDto.getFieldName()).isEqualTo(fieldName),
                () ->         assertThat(drawingFieldDto.getDescription()).isEqualTo(fieldName),
                () ->         assertThat(drawingFieldDto.getStatus()).isEqualTo(DrawingFieldStatus.USED)
        );
        verify(drawingFieldRepository).findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED));
    }

    @Test
    @DisplayName("[실패] 그림 분야 상세 조회- 해당 아이디에 해당하는 데이터가 없는 경우")
    public void givenWrongDrawingFieldId_whenGetOneDrawingFieldDto_thenNotFoundDrawingFieldError(){
        //given
        ErrorCode errorCode = DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD;
        final long wrongDrawingFieldId = 999L;

        when(drawingFieldRepository.findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED)))
                .thenReturn(
                        Optional.empty()
                );
        //when

        //then
        final DrawingFieldException drawingFieldException = assertThrows(DrawingFieldException.class,
                () -> drawingFieldService.findDrawField(wrongDrawingFieldId)
        );
        assertAll(
                () -> assertThat(drawingFieldException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(drawingFieldException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(drawingFieldRepository).findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED));
    }

    @Test
    @DisplayName("[성공] 그림 분야 추가")
    public void givenCreateDrawingFieldDto_whenCreateDrawingField_thenNothing(){
        //given
        final String fieldName = "캐릭터 디자인";
        when(drawingFieldRepository.save(any(DrawingField.class)))
            .thenReturn(
                    getDrawingField(fieldName)
            );
        when(drawingFieldRepository.findByFieldName(anyString()))
            .thenReturn(
                    Optional.empty()
            );

        //when
        drawingFieldService.addDrawingField(
                givenDrawingFieldDto(fieldName, fieldName)
        );

        //then
        verify(drawingFieldRepository).findByFieldName(anyString());
        verify(drawingFieldRepository).save(any(DrawingField.class));
    }

    private CreateDrawingFieldDto givenDrawingFieldDto(String fieldName, String description) {
        return CreateDrawingFieldDto.builder()
                .fieldName(fieldName)
                .description(description)
                .build();
    }

    @Test
    @DisplayName("[실패] 그림 분야 추가 - 중복된 필드 명이 존재 하는 경우")
    public void givenDuplicatedCreateDrawingFieldDto_whenCreateDrawingField_thenDuplicatedDrawingFieldNameError() {
        //given
        final String duplicatedFieldName = "캐릭터 디자인";
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.DUPLICATED_DRAWING_FIELD_NAME;
        when(drawingFieldRepository.findByFieldName(anyString()))
                .thenReturn(
                        Optional.ofNullable(
                                getDrawingField(duplicatedFieldName)
                        )
                );

        //when
        final DrawingFieldException drawingFieldException = assertThrows(DrawingFieldException.class,
                () -> drawingFieldService.addDrawingField(
                        givenDrawingFieldDto(duplicatedFieldName, duplicatedFieldName)
                )
        );

        //then
        assertAll(
                () -> assertThat(drawingFieldException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(drawingFieldException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(drawingFieldRepository).findByFieldName(anyString());
        verify(drawingFieldRepository, never()).save(any(DrawingField.class));
    }

    @Test
    @DisplayName("[성공] 그림 분야 수정")
    public void givenDrawingFieldIdAndUpdateDrawingFieldDto_whenUpdateDrawingField_thenNothing(){
        //given
        final String fieldName = "캐릭터 디자인";
        final long drawingFieldId = 1L;

        final DrawingField drawingField = getDrawingField(fieldName);
        when(drawingFieldRepository.findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED)))
                .thenReturn(
                        Optional.ofNullable(
                                drawingField
                        )
                );

        //when
        drawingFieldService.updateDrawingField(
                drawingFieldId,
                givenUpdateDrawingFieldDto("일러스트", "일러스트 설명", DrawingFieldStatus.DISABLED)
        );

        //then
        assertThat(drawingField)
                .hasFieldOrPropertyWithValue("fieldName", "일러스트")
                .hasFieldOrPropertyWithValue("description", "일러스트 설명")
                .hasFieldOrPropertyWithValue("status", DrawingFieldStatus.DISABLED)
        ;
        verify(drawingFieldRepository).findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED));
    }

    private UpdateDrawingFieldDto givenUpdateDrawingFieldDto(String fieldName, String description, DrawingFieldStatus status) {
        return  UpdateDrawingFieldDto.builder()
                .fieldName(fieldName)
                .description(description)
                .status(status)
                .build();
    }

    @Test
    @DisplayName("[실패] 그림 분야 수정 - 해당 아이디가 없는 경우")
    public void givenWrongDrawingFieldId_whenUpdateDrawingField_thenNotFoundDrawingFieldError(){
        //given
        final long wrongDrawingFieldId = 999L;
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD;

        when(drawingFieldRepository.findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED)))
                .thenReturn(
                        Optional.empty()
                );

        //when
        final DrawingFieldException drawingFieldException = assertThrows(DrawingFieldException.class,
                () -> drawingFieldService.updateDrawingField(
                        wrongDrawingFieldId, givenUpdateDrawingFieldDto("일러스트", "일러스트 설명", DrawingFieldStatus.DISABLED)
                )
        );

        //then
        assertAll(
                () -> assertThat(drawingFieldException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(drawingFieldException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(drawingFieldRepository).findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED));
    }

    @Test
    @DisplayName("[성공] 그림 분야 삭제")
    public void givenDrawingFieldId_whenDeleteDrawingField_thenNothing(){
        //given
        final String fieldName = "캐릭터 디자인";
        final long drawingFieldId = 1L;

        DrawingField drawingField = getDrawingField(fieldName);
        DrawingFieldStatus beforeStatus = drawingField.getStatus();
        when(drawingFieldRepository.findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED)))
                .thenReturn(
                        Optional.ofNullable(
                                drawingField
                        )
            );

        //when
        drawingFieldService.removeDrawingField(
                drawingFieldId
        );

        //then
        assertAll(
                () -> assertThat(beforeStatus).isEqualTo(DrawingFieldStatus.USED),
                () -> assertThat(drawingField.getStatus()).isEqualTo(DrawingFieldStatus.DISABLED)
        );
        verify(drawingFieldRepository).findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED));
    }

    @Test
    @DisplayName("[실패] 그림 분야 삭제 - 해당 아이디를 가진 데이터가 없는 경우")
    public void givenDrawingFieldId_whenDeleteDrawingField_thenNotFoundDrawingFieldError(){
        //given
        final long wrongDrawingFieldId = 999L;
        final DrawingFieldErrorCode errorCode = DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD;

        when(drawingFieldRepository.findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED)))
                .thenReturn(
                        Optional.empty()
                );

        //when
        final DrawingFieldException drawingFieldException = assertThrows(DrawingFieldException.class,
                () -> drawingFieldService.removeDrawingField(
                        wrongDrawingFieldId
                )
        );

        //then
        assertAll(
                () -> assertThat(drawingFieldException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(drawingFieldException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(drawingFieldRepository).findByDrawingFieldIdAndStatusNot(anyLong(), eq(DrawingFieldStatus.DISABLED));
        verify(drawingFieldRepository, never()).delete(any(DrawingField.class));
    }

}