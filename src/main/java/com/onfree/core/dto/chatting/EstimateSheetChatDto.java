package com.onfree.core.dto.chatting;

import com.onfree.core.entity.realtimerequset.UseType;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class EstimateSheetChatDto {
    private String title; // 프로젝트 제목

    private String content; // 프로젝트 내용

    private LocalDate startDate; // 시작 일

    private LocalDate endDate; // 종료 일

    private Long price; // 결제 금액

    private UseType useType; // 용도

    private String referenceLink; // 참고 링크

    private Boolean adult; // 성인용 유무
}
