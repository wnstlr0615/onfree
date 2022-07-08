package com.onfree.core.entity.payment;

import com.onfree.core.entity.portfolio.Portfolio;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cancel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cancelId;

    private Long cancelAmount; // 취소 금액
    private String cancelReason; // 취소 사유
    private LocalDateTime canceledAt; // 취소 날짜와 시간
    private Long taxAmount; // 과세 처리된 금액
    private Long taxFreeAmount; // 면세 처리된 금액
    private Long refundableAmount; // 결제 취소후 환불 가능한 잔액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    //== 비즈니스 메서드 ==//
    public void setPayment(Payment payment) {
        if(payment != null){
            payment.getCancels().remove(this);
        }
        this.payment = payment;

        if(!payment.getCancels().contains(this)){
            payment.getCancels().add(this);
        }
    }


}
