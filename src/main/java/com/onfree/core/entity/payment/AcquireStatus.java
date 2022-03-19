package com.onfree.core.entity.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AcquireStatus {
    READY("매입 대기"),
    REQUESTED("매입 요청 됨"),
    COMPLETED("매입 완료"),
    CANCEL_REQUESTED("매입 취소 요청됨"),
    CANCELED("매입 취소 완료")
    ;
    private final String description;
}
