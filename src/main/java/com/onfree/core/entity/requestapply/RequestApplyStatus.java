package com.onfree.core.entity.requestapply;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestApplyStatus {
    READY("의뢰 준비 완료", 100),
    RECEIVED_STATEMENT("견적서(제안서)를 전달 받은 상태", 200),
    STATEMENT_ACCEPT("제안서 수락 상태", 200),

    DEPOSIT_DOWN_PAYMENT("계약금 입금 완료", 200),

    PAYMENT_REQUEST("작업 완료 후 결제 지급 요청", 200),
    PAYMENT_COMPLETED("작업 완료에 따른 결제 대금 지급 완료", 300),

    /** 추가 금 관련 */
    EXTRA_MONEY_REQUEST("추가금 요청 상태", 200),
    EXTRA_MONEY_COMPLETED("추가금 지급 상태", 300),

    /** 환불 관련 */
    REFUND_REQUEST("환불 요청 하기", 300),
    REFUND_COMPLETED("환불 완료", 400),

    CANCEL("요청 불발로 인한 의뢰 취소", 500),
    ;
    private final String description;
    private final int statusCode;

}
