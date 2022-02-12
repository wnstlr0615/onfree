package com.onfree.core.service;

import com.onfree.common.error.code.DrawingFieldErrorCode;
import com.onfree.common.error.exception.DrawingFieldException;
import com.onfree.core.dto.drawingfield.CreateDrawingFieldDto;
import com.onfree.core.dto.drawingfield.DrawingFieldDto;
import com.onfree.core.dto.drawingfield.UpdateDrawingFieldDto;
import com.onfree.core.entity.DrawingField;
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
        saveDrawingField("캐릭터 디자인", "캐릭터 디자인");
        saveDrawingField("버츄얼 디자인", "버츄얼 디자인");
        saveDrawingField("일러스트", "일러스트");
        saveDrawingField("게임삽화/원화", "게임삽화/원화");
        saveDrawingField("만화, 소설표지", "만화, 소설표지");
        saveDrawingField("애니메이팅/밈", "애니메이팅/밈");
        saveDrawingField("파츠 제작", "파츠 제작");
        saveDrawingField("19+", "19+");
    }

    private void saveDrawingField(String fieldName, String description) {
        drawingFieldRepository.save(
                createDrawingField(fieldName, description)
        );
    }

    private DrawingField createDrawingField(String fieldName, String description) {
        return DrawingField.builder()
                .fieldName(fieldName)
                .description(description)
                .top(false)
                .disabled(false)
                .build();
    }
    /** 그림분야 조회 */
    public List<DrawingFieldDto> getDrawFieldDtoList() {
        return getAllDrawingFieldDtoList();
    }

    private List<DrawingFieldDto> getAllDrawingFieldDtoList() {
        final List<DrawingField> getDrawingFieldList = drawingFieldRepository.findAllByOrderByTopDesc();
        if(getDrawingFieldList.isEmpty()){
            throw new DrawingFieldException(DrawingFieldErrorCode.DRAWING_FIELD_EMPTY);
        }
        return getDrawingFieldList
                .stream()
                .map(DrawingFieldDto::fromEntity)
                .collect(Collectors.toList());
    }
    /**그림 분야 추가*/
    @Transactional
    @CacheEvict(value = "drawingFieldAll", allEntries = true)
    public void createDrawingField(CreateDrawingFieldDto createDrawingFieldDto) {
        final DrawingField drawingField = createDrawingFieldDto.toEntity();
        validDuplicatedDrawinFieldName(drawingField);
        drawingFieldRepository.save(drawingField);
    }

    private void validDuplicatedDrawinFieldName(DrawingField drawingField) {
        final Optional<DrawingField> optionalDrawingField = drawingFieldRepository
                .findByFieldName(drawingField.getFieldName());
        if(optionalDrawingField.isPresent()){
            throw new DrawingFieldException(DrawingFieldErrorCode.DUPLICATED_DRAWING_FIELD_NAME);
        }
    }
    /** 그림 분야 수정*/
    @Transactional
    public void updateDrawingField(Long drawingFieldId, UpdateDrawingFieldDto updateDrawingFieldDto) {
        final DrawingField drawingField = getDrawingField(drawingFieldId);
        drawingField.modifiedDrawingField(updateDrawingFieldDto.toEntity());
    }

    private DrawingField getDrawingField(Long drawingFieldId) {
        return drawingFieldRepository.findById(drawingFieldId).orElseThrow(
                () -> new DrawingFieldException(DrawingFieldErrorCode.NOT_FOUND_DRAWING_FIELD)
        );
    }

    /** 그림 분야 삭제*/
    @Transactional
    public void deleteDrawingField(Long drawingFieldId) {
        final DrawingField drawingField = getDrawingField(drawingFieldId);
        drawingFieldRepository.delete(drawingField);
    }

    public DrawingFieldDto getOneDrawField(Long drawingFieldId) {
        return getDrawFieldDto(
                getDrawingField(drawingFieldId)
                );
    }

    private DrawingFieldDto getDrawFieldDto(DrawingField drawingField) {
        return DrawingFieldDto.fromEntity(drawingField);
    }
}
