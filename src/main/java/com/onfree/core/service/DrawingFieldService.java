package com.onfree.core.service;

import com.onfree.common.error.code.DrawingFieldErrorCode;
import com.onfree.common.error.exception.DrawingFieldException;
import com.onfree.core.dto.drawingfield.CreateDrawingFieldDto;
import com.onfree.core.dto.drawingfield.DrawingFieldDto;
import com.onfree.core.dto.drawingfield.UpdateDrawingFieldDto;
import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
import com.onfree.core.repository.DrawingFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DrawingFieldService {
    private final DrawingFieldRepository drawingFieldRepository;

    @PostConstruct
    private void init(){
        saveDrawingField("캐릭터 디자인", "캐릭터 디자인", DrawingFieldStatus.USED);
        saveDrawingField("버츄얼 디자인", "버츄얼 디자인", DrawingFieldStatus.USED);
        saveDrawingField("일러스트", "일러스트", DrawingFieldStatus.USED);
        saveDrawingField("게임삽화/원화", "게임삽화/원화", DrawingFieldStatus.USED);
        saveDrawingField("만화, 소설표지", "만화, 소설표지", DrawingFieldStatus.USED);
        saveDrawingField("애니메이팅/밈", "애니메이팅/밈", DrawingFieldStatus.USED);
        saveDrawingField("파츠 제작", "파츠 제작", DrawingFieldStatus.USED);
        saveDrawingField("19+", "19+", DrawingFieldStatus.USED);
    }

    private void saveDrawingField(String fieldName, String description, DrawingFieldStatus status) {
        saveDrawingField(
                createDrawingField(fieldName, description, status)
        );
    }

    private DrawingField createDrawingField(String fieldName, String description, DrawingFieldStatus status) {
        return DrawingField.createDrawingField(fieldName,description, status);
    }

    /** 그림 분야 상세 조회*/
    public DrawingFieldDto findDrawField(Long drawingFieldId) {
        return getDrawingFieldDto( // DrawingFieldDto로 변환
                getDrawingField(drawingFieldId)// 그림분야 조회
        );
    }

    /** 그림분야 전체 조회 */
    public List<DrawingFieldDto> findAllDrawingField() {
        return getDrawingFieldDtos( // DrawingFieldDto로 변환
                    getDrawingFields() // DrawingFields 조회
        );
    }

    private List<DrawingFieldDto> getDrawingFieldDtos(List<DrawingField> getDrawingFieldList) {
        return getDrawingFieldList.stream()
                .map(DrawingFieldDto::fromEntity)
                .collect(Collectors.toList());
    }

    private List<DrawingField> getDrawingFields() {
        final List<DrawingField> getDrawingFieldList = drawingFieldRepository.findAllByStatusNot(DrawingFieldStatus.DISABLED);
        if(getDrawingFieldList.isEmpty()){
            throw new DrawingFieldException(DrawingFieldErrorCode.DRAWING_FIELD_EMPTY);
        }
        return getDrawingFieldList;
    }

    /**그림 분야 추가*/
    @Transactional
    public DrawingFieldDto addDrawingField(CreateDrawingFieldDto createDrawingFieldDto) {
        return getDrawingFieldDto( // DrawingFieldDto로 변환
                saveDrawingField( // DrawingField 저장
                    validDuplicatedDrawinFieldName( // 중복 검사
                            createDrawingFieldFromDto(createDrawingFieldDto) //DTO로 부터 DrawingField 생성
                    )
                )
        );
    }
    private DrawingField createDrawingFieldFromDto(CreateDrawingFieldDto createDrawingFieldDto) {
        return createDrawingFieldDto.toEntity();
    }

    private DrawingField validDuplicatedDrawinFieldName(DrawingField drawingField) {
        final Optional<DrawingField> optionalDrawingField = drawingFieldRepository
                .findByFieldName(drawingField.getFieldName());
        if(optionalDrawingField.isPresent()){
            throw new DrawingFieldException(DrawingFieldErrorCode.DUPLICATED_DRAWING_FIELD_NAME);
        }
        return drawingField;
    }

    private DrawingField saveDrawingField(DrawingField drawingField) {
        return drawingFieldRepository.save(drawingField);
    }

    private DrawingFieldDto getDrawingFieldDto(DrawingField drawingField) {
        return DrawingFieldDto.fromEntity(drawingField);
    }

    /** 그림 분야 수정*/
    @Transactional
    @CacheEvict(value = "drawingFieldAll", allEntries = true)
    public void updateDrawingField(Long drawingFieldId, UpdateDrawingFieldDto updateDrawingFieldDto) {
        final DrawingField drawingField = getDrawingField(drawingFieldId);
        drawingField.updateDrawingField(updateDrawingFieldDto.toEntity());
    }

    private DrawingField getDrawingField(Long drawingFieldId) {
        return drawingFieldRepository.findByDrawingFieldIdAndStatusNot(drawingFieldId, DrawingFieldStatus.DISABLED)
                .orElseThrow(
                        () -> new DrawingFieldException(DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD)
                );
    }

    /** 그림 분야 삭제*/
    @Transactional
    @CacheEvict(value = "drawingFieldAll", allEntries = true)
    public void removeDrawingField(Long drawingFieldId) {
        deleteDrawingField( // 그림분야 제거
                getDrawingField(drawingFieldId) // 그림분야 조회
        );
    }

    private void deleteDrawingField(DrawingField drawingField) {
        drawingField.delete();
    }



}
