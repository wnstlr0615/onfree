package com.onfree.core.entity.chatting;

import com.onfree.core.entity.realtimerequset.UseType;
import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue(value = "estimate")
public class EstimateSheetChat extends Chatting{
    //TODO 프로젝트 기본 속성들 임베디드 처리하기(실시간 의뢰, 제안서, 의뢰서)
    @Column(nullable = false)
    private String title; // 프로젝트 제목

    @Column(nullable = false)
    private String content; // 프로젝트 내용

    @Column(nullable = false)
    private LocalDate startDate; // 시작 일

    @Column(nullable = false)
    private LocalDate endDate; // 종료 일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseType useType; // 용도

    private String referenceLink; // 참고 링크

    @Column(nullable = false)
    private Boolean adult; // 성인용 유무

    @Builder
    public EstimateSheetChat(RequestApply requestApply, User sender, User recipient, String title, String content, LocalDate startDate, LocalDate endDate, UseType useType, String referenceLink, Boolean adult) {
        super(sender, recipient, requestApply);
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.useType = useType;
        this.referenceLink = referenceLink;
        this.adult = adult;
    }

    public static EstimateSheetChat createEstimateSheetChat(
            RequestApply requestApply, User sender, User recipient, String title,
            String content, LocalDate startDate, LocalDate endDate, UseType useType, String referenceLink, Boolean adult
    ) {
        return EstimateSheetChat.builder()
                .requestApply(requestApply)
                .sender(sender)
                .recipient(recipient)
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .useType(useType)
                .referenceLink(referenceLink)
                .adult(adult)
                .build();

        }
}
