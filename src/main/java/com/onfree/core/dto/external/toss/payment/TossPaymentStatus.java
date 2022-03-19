package com.onfree.core.dto.external.toss.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TossPaymentStatus {
    READY("준비됨"),
    IN_PROGRESS("진행중"),
    WAITING_FOR_DEPOSIT("가상계좌 입금 대기중"),
    DONE("결제완료됨"),
    CANCELED("결제가 취소됨"),
    PARTIAL_CANCELED("결제가 부분 취소됨"),
    ABORTED("카드 자동 결제 혹은 키인 결제를 할 때 결제 승인에 실패함"),
    EXPIRED("유효시간(30분)이 지나 거래가 취소됨")
    ;
    private final String description;
}
