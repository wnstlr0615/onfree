package com.onfree.core.dto.external.toss.payment;

import com.onfree.core.entity.payment.AcquireStatus;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class CardDto {
    private String company; // 카드사 코드
    private String number; // 카드 번호 (일부 마스킹 처리됨)
    private Integer installmentPlanMonths; //할부 개월 수
    private String approveNo; // 카드사 승인 번호
    private Boolean useCardPoint; // 카드사 포인트 사용 여부
    private String cardType; // 카드 타입(신용, 체크, 기프트)
    private String ownerType; // 카드 소유자 타입(개인, 법인)
    private String receiptUrl; //  카드 매출 전표 조회 페이지 주소
    private AcquireStatus acquireStatus; //카드 결제의 매입상태
    private Boolean isInterestFree; // 무이자 할부 적용 여부
}
