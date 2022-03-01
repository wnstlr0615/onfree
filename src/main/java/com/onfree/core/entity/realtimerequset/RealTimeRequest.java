package com.onfree.core.entity.realtimerequset;

import com.onfree.common.model.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RealTimeRequest extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long realTimeRequestId;

    private String title; // 프로젝트 제목

    private String content; // 프로젝트 내용

    private LocalDate startDate; // 시작 일

    private LocalDate endDate; // 종료 일

    @Enumerated(EnumType.STRING)
    private UseType useType; // 용도

    private String referenceLink; // 참고 링크

    private Boolean adult; // 성인용 유무

    private RequestStatus status; // 실시간 의뢰 상태
}
