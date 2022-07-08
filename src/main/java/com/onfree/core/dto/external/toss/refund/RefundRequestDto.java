package com.onfree.core.dto.external.toss.refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
/* 환불 신청 DTO */
public class RefundRequestDto {
    private String cancelReason; //취소사유(필수)
    private Long cancelAmount; // 환불 금액 없을 경우 전액 환불
    private Long taxAmount; // 과세 금액
    private Long taxFreeAmount; // 면세 금액
    private Long refundableAmount; // 현재 환불 가능한 잔액 정보
    private RefundReceiveAccount refundReceiveAccount; // 결제 취소후 환불될 계좌 정보 (가상계좌에서만 필수)

    //== 생성 메서드 ==//
    public static RefundRequestDto createRefundRequestDto(String cancelReason){
        return RefundRequestDto.builder()
                .cancelReason(cancelReason)
                .build();
    }
}