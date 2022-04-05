package com.onfree.core.entity.chatting;

import com.onfree.core.entity.realtimerequset.UseType;
import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
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

    @Column(nullable = false)
    private Long estimatedAmount; //견적 금액

    @Column(nullable = false)
    private Long paymentAmount; //결제 금액

    @Column(nullable = false)
    private String conditionNote; // 조건 사항(수정 범위 및 횟수)

    @Column(nullable = false)
    private String offerResult; // 제공하는 결과물

    @Column(nullable = false)
    private String orderId; // 토스 결제용 orderId

    @Column(nullable = false)
    private Boolean ordered;

    @Builder
    public EstimateSheetChat(
            RequestApply requestApply, User sender, User receiver, String title, String content, Long estimatedAmount,
            LocalDate startDate, LocalDate endDate, String conditionNote, String offerResult ,String orderId) {
        super(sender, receiver, requestApply);
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.estimatedAmount = estimatedAmount;
        this.paymentAmount = estimatedAmount + (estimatedAmount / 10);
        this.conditionNote = conditionNote;
        this.offerResult = offerResult;
        this.orderId = orderId;
        this.ordered = false;
    }

    //== 생성 메서드 ==//
    public static EstimateSheetChat createEstimateSheetChat(
            RequestApply requestApply, User sender, User receiver, String title, String content, Long estimatedAmount,
            LocalDate startDate, LocalDate endDate,  String conditionNote, String offerResult,  String orderId
    ) {
        return EstimateSheetChat.builder()
                .requestApply(requestApply)
                .sender(sender)
                .receiver(receiver)
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .estimatedAmount(estimatedAmount)
                .conditionNote(conditionNote)
                .offerResult(offerResult)
                .orderId(orderId)
                .build();

        }
        //== 비즈니스 메서드 ==//
    public void ordered(){
        this.ordered = true;
    }
}
