package com.onfree.core.repository;

import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DrawingFieldRepository extends JpaRepository<DrawingField, Long> {
    /** 입력받은  status가 아니고 id와 일치하는 그림분야 조회*/
    Optional<DrawingField> findByDrawingFieldIdAndStatusNot(Long drawingFIeldId, DrawingFieldStatus status);

    /** 삭제 처리 되지 않은 데이터중 Top이 true인 그림분야를 우선으로 하는 모든 그림 분야 가져오기*/
    @Cacheable(value = "drawingFieldAll")
    @Query(value = "select df from DrawingField as df " +
            "where df.status not in ('TEMP', 'DISABLED') " +
            "order by case when df.status = 'TOP' then 1 " +
            "else 2 end")
    List<DrawingField> findAllByStatusNotDisabledAndTempOrderByTopDesc();

    /** 입력받은 status가 아닌 모든 그림분야 조회*/
    List<DrawingField> findAllByStatusNot(DrawingFieldStatus status);
    /** 필드이름(unique)으로 그림분야 가져오기 */
    Optional<DrawingField> findByFieldName(String fieldName);

    /** 삭제 처리 되지 않은 그림 중 해당 그림 분야 ID와 일치하는 그림분야 가져오기 */
    @Query(value = "select df from DrawingField as df " +
            "where df.status not in ('DISABLED', 'TEMP') " +
            "and df.drawingFieldId in :drawingFieldId " +
            "order by case when df.status = 'TOP' then 1 " +
            "else 2 end")
    List<DrawingField> findAllByStatusNotDisabledAndTempDrawingFieldIdIn(@Param("drawingFieldId") List<Long> drawingFieldId);
}
