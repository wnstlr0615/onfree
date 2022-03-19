package com.onfree.core.entity.requestapply;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestApplyStatus {
    REQUEST_APPLY_CRATED("의뢰 생성 완료", 100),
    REQUEST_APPLY_SENT_QUOTATION("견적서(제안서)를 전달 받은 상태", 200),
    REQUEST_APPLY_ACCEPT("견적(제안서) 수락 후 작업 중", 200),
    REQUEST_APPLY_DEPOSIT_PAYMENT("계약금 입금 완료", 200),
    REQUEST_APPLY_PAYMENT_REQUEST ("작업 완료 후 결제 지급 요청", 200),
    REQUEST_APPLY_PAYMENT_COMPLETED("작업 완료에 따른 결제 대금 지급 완료", 300),

    /** 추가 금 관련 */
    REQUEST_APPLY_EXTRA_MONEY_REQUEST("추가금 요청 상태", 200),
    REQUEST_APPLY_EXTRA_MONEY_COMPLETED("추가금 지급 상태", 300),

    /** 환불 관련 */
    REQUEST_APPLY_REFUND_REQUEST("환불 요청 하기", 300),
    REQUEST_APPLY_REFUND_COMPLETED("환불 완료", 400),

    /** 신고 상태 */
    //REQUEST_APPLY_REPORTED("의뢰자에 의해 신고 상태", 200),
    //REQUEST_APPLY_REPORTED_EXTERNAL_TRANSACTION("의뢰자에 의해 신고 상태", 200),

    REQUEST_APPLY_CANCEL("요청 불발로 인한 의뢰 취소", 500),
    ;
    private final String description;
    private final int statusCode;
}
