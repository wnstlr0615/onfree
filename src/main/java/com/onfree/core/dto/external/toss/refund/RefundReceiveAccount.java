package com.onfree.core.dto.external.toss.refund;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefundReceiveAccount {
    private String bank; // 환분 계좌 은행
    private String accountNumber; // 환불계좌
    private String holderName; //취소 금액을 환불받을 계좌의 예금주 이름
}
