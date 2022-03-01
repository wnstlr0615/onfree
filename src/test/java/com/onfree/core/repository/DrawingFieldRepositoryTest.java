package com.onfree.core.repository;

import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DataJpaTest
@ActiveProfiles("test")
class DrawingFieldRepositoryTest {
    @Autowired
    DrawingFieldRepository drawingFieldRepository;

    @AfterEach
    public void tearDown(){
        drawingFieldRepository.deleteAll();
    }

    private DrawingField saveDrawingField(String fieldName, DrawingFieldStatus status) {
        return drawingFieldRepository.save(
                DrawingField.createDrawingField(fieldName, fieldName, status)
        );
    }
    @Test
    @DisplayName("그림분야 상태가 TEMP와 DISABLED를 제외한 결과를 반환 하면서 Top 상태인 그림분야를 앞으로 정렬해서 반환해주는지 테스트 ")
    public void givenNothing_whenFindAllByStatusNotDisabledOrderByTopDesc_thenSortedList(){

        //given
        // 그림분야 TOP 2개 , USED 4개, TEMP 4개, DISABLED 4개 등록

        saveDrawingField("그림분야1", DrawingFieldStatus.USED);
        saveDrawingField("그림분야2", DrawingFieldStatus.TEMP);
        saveDrawingField("그림분야3", DrawingFieldStatus.TEMP);
        saveDrawingField("그림분야4", DrawingFieldStatus.DISABLED);
        saveDrawingField("그림분야5", DrawingFieldStatus.DISABLED);
        saveDrawingField("그림분야6", DrawingFieldStatus.TOP);
        saveDrawingField("그림분야7", DrawingFieldStatus.TOP);
        saveDrawingField("그림분야8", DrawingFieldStatus.DISABLED);
        saveDrawingField("그림분야9", DrawingFieldStatus.TEMP);
        saveDrawingField("그림분야10", DrawingFieldStatus.USED);
        saveDrawingField("그림분야11", DrawingFieldStatus.DISABLED);
        saveDrawingField("그림분야12", DrawingFieldStatus.TEMP);
        saveDrawingField("그림분야13", DrawingFieldStatus.USED);
        saveDrawingField("그림분야14", DrawingFieldStatus.USED);

        //when
        List<DrawingField> sortedDrawingFields = drawingFieldRepository.findAllByStatusNotDisabledAndTempOrderByTopDesc();

        //then
        assertAll(
            () -> assertThat(
                    sortedDrawingFields.stream()
                    .anyMatch(drawingField -> drawingField.getStatus().equals(DrawingFieldStatus.DISABLED))
                ).isFalse().as("반환 그림 분야들중 상태가 disabled인 그림분야가 있는지 검사"),
            () -> assertThat(
                        sortedDrawingFields.stream()
                            .anyMatch(drawingField -> drawingField.getStatus().equals(DrawingFieldStatus.TEMP))
                ).isFalse().as("반환 그림 분야들중 상태가 Temp인 그림분야가 있는지 검사"),

            () -> assertThat(sortedDrawingFields.get(0).getStatus()).isEqualTo(DrawingFieldStatus.TOP)
                .as("첫번째 그림분야 Status가 TOP인지 확인"),
            () -> assertThat(sortedDrawingFields.get(1).getStatus()).isEqualTo(DrawingFieldStatus.TOP)
                .as("두번째 그림분야가 Status 가 TOP인지 확인"),
            () -> assertThat(sortedDrawingFields.get(2).getStatus()).isNotEqualTo(DrawingFieldStatus.TOP)
                .as("세번째 그림분야 Status가 Top이 아닌 지 확인"),
            () -> assertThat(sortedDrawingFields.size()).isEqualTo(6)
                .as("반환된 그림분야 14개 중 상태가 disabled와 TEMP인 8개를 제외한 6개인 지 확인")
        );
    }

    @Test
    @DisplayName("입력받은 그림분야 pk중 그림분야 상태가 TEMP와 DISABLED를 제외한 결과를 반환 하면서 Top 상태인 그림분야를 앞으로 정렬해서 반환해주는지 테스트 ")
    public void givenDrawingFieldIds_whenFindAllByStatusNotDisabledAndDrawingFieldIdIn_thenSortedList(){
        //given
        //TEMP 2개 USED 2개 DISABLED 2개 TOP 1개
        saveDrawingField("그림분야1", DrawingFieldStatus.USED);
        saveDrawingField("그림분야2", DrawingFieldStatus.TEMP);
        saveDrawingField("그림분야3", DrawingFieldStatus.TEMP);
        saveDrawingField("그림분야4", DrawingFieldStatus.DISABLED);
        saveDrawingField("그림분야5", DrawingFieldStatus.DISABLED);
        saveDrawingField("그림분야6", DrawingFieldStatus.TOP);
        saveDrawingField("그림분야3", DrawingFieldStatus.USED);

        List<Long> drawingFieldIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);

        //when
        List<DrawingField> sortedDrawingFields = drawingFieldRepository.findAllByStatusNotDisabledAndTempDrawingFieldIdIn(drawingFieldIds);

        //then
        assertAll(
                () -> assertThat(
                        sortedDrawingFields.stream()
                                .anyMatch(drawingField -> drawingField.getStatus().equals(DrawingFieldStatus.DISABLED))
                ).isFalse().as("반환 그림 분야들중 상태가 disabled인 그림분야가 있는지 검사"),
                () -> assertThat(
                        sortedDrawingFields.stream()
                                .anyMatch(drawingField -> drawingField.getStatus().equals(DrawingFieldStatus.TEMP))
                ).isFalse().as("반환 그림 분야들중 상태가 Temp인 그림분야가 있는지 검사"),
                () -> assertThat(sortedDrawingFields.get(0).getStatus()).isEqualTo(DrawingFieldStatus.TOP)
                        .as("첫번째 그림분야 Status가 TOP인지 확인"),
                () -> assertThat(sortedDrawingFields.get(1).getStatus()).isNotEqualTo(DrawingFieldStatus.TOP)
                        .as("두번 째 그림분야 Status가 Top이 아닌 지 확인"),
                () -> assertThat(sortedDrawingFields.size()).isEqualTo(3)
                        .as("반환된 그림분야 7개 중 상태가 DISABLED와 TEMP인 4개를 제외한 3개인 지 확인")
        );
    }

}