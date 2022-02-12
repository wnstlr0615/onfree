package com.onfree.core.repository;

import com.onfree.core.entity.DrawingField;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrawingFieldRepository extends JpaRepository<DrawingField, Long> {
    /** Top이 True인 그림분야를 우선으로 하여 모든 그림분야 가져오기 */
    List<DrawingField> findAllByOrderByTopDesc();

    /** 삭제 처리 되지 않은 데이터중 Top이 true인 그림분야를 우선으로 하는 모든 그림 분야 가져오기*/
    @Cacheable(value = "drawingFieldAll")
    List<DrawingField> findAllByDisabledIsFalseOrderByTopDesc();

    /** 필드이름(unique)으로 그림분야 가져오기 */
    Optional<DrawingField> findByFieldName(String fieldName);

    /** 삭제 처리 되지 않은 그림 중 해당 그림 분야 ID와 일치하는 그림분야 가져오기 */
    List<DrawingField> findAllByDisabledIsFalseAndDrawingFieldIdIn(List<Long> drawingFieldId);
}
