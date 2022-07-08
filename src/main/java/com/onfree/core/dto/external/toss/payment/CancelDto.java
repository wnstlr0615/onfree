package com.onfree.core.dto.external.toss.payment;

import com.onfree.core.entity.payment.Cancel;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class CancelDto {
    private Long cancelAmount; // 취소 금액
    private String cancelReason; // 취소 사유
    private Date canceledAt; // 취소 날짜와 시간
    private Long taxAmount; // 과세 처리된 금액
    private Long taxFreeAmount; // 면세 처리된 금액
    private Long refundableAmount; // 결제 취소후 환불 가능한 잔액

    //== 생성 메서드 ==//
    public static CancelDto createCancelDto(Long cancelAmount, String cancelReason, Date canceledAt, Long taxAmount, Long taxFreeAmount, Long refundableAmount){
        return CancelDto.builder()
                .cancelAmount(cancelAmount)
                .cancelReason(cancelReason)
                .canceledAt(canceledAt)
                .taxAmount(taxAmount)
                .taxFreeAmount(taxFreeAmount)
                .refundableAmount(refundableAmount)
                .build();
    }
    public Cancel toCancel(){
        LocalDateTime canceledAtLocalDateTime = canceledAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Cancel.builder()
                .cancelAmount(cancelAmount)
                .cancelReason(cancelReason)
                .canceledAt(canceledAtLocalDateTime)
                .taxAmount(taxAmount)
                .taxFreeAmount(taxFreeAmount)
                .refundableAmount(refundableAmount)
                .build();
    }

}
