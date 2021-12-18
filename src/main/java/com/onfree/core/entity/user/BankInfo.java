package com.onfree.core.entity.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankInfo {
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BankName bankName; //은행명

    @Column(nullable = false, length = 20)
    private String accountNumber; // 계좌번호
}
