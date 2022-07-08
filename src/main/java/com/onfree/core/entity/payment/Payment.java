package com.onfree.core.entity.payment;

import com.onfree.common.model.BaseTimeEntity;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.requestapply.RequestApply;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    //private String version; // Payment 객체 응답 버전
    @Column(unique = true)
    private String paymentKey; // 결제 건에 대한 고유 키값

    @Column(unique = true, nullable = false)
    private String orderId; // 가맹점에서 발급한 고유 ID
    private String orderName; // 결제에 대한 주문명
    private String currency; //결제 할 때 사용한 통화 단위 원한인 KRW만 가능
    private String method; // 결제 수단
    private Long totalAmount; // 총 결제 금액
    private Long balanceAmount; // 취소할 수 있는 금액 (잔고)
    private Long suppliedAmount; //공급 가액
    private Long vat; // 부가세

    @Enumerated(EnumType.STRING)
    private TossPaymentStatus status; // 결제 처리 상태

    private LocalDateTime requestedAt; // 결제 요청이 일어난 날짜와 시간 정보
    private LocalDateTime approvedAt; // 결제 승일 시간
    private Boolean useEscrow; //에스크로 사용 여부
    private Boolean cultureExpense; // 문화비 지출 여부
    private String secret; // 가상계좌로 결제할 때 전달되는 입금 콜백을 검증하기 위한 값
    private String type; // 결제 타입 정보 (NORMAL, BILLING, CONNECTPAY) 중 하나
    private String easyPay; // 간편 결제 사용시 간편 결제 타입 정보
    private Long taxFreeAmount; //면세 금액

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "card_id")
    private Card card;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payment", cascade = CascadeType.ALL)
    private final List<Cancel> cancels = new ArrayList<>(); // 취소 관련 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_apply_id")
    private RequestApply requestApply;

    public void setRequestApply(@NonNull RequestApply requestApply) {
        this.requestApply = requestApply;
    }

    //== 연관 관계 메서드 ==//
    public void addCancel(Cancel cancel){
        cancels.add(cancel);
        if(cancel.getPayment() != this){
            cancel.setPayment(this);
        }
    }


    public void updatePayment(Payment payment) {
        verifyEqualsPaymentKeyAndOrderId(payment.getPaymentKey(), payment.getOrderId());
        this.orderName = payment.getOrderName();
        this.currency = payment.getCurrency();
        this.method = payment.getMethod();
        this.totalAmount = payment.getTotalAmount();
        this.balanceAmount = payment.getBalanceAmount();
        this.suppliedAmount = payment.getSuppliedAmount();
        this.vat = payment.getVat();
        this.status = payment.getStatus();
        this.requestedAt = payment.getRequestedAt();
        this.approvedAt = payment.getApprovedAt();
        this.useEscrow = payment.getUseEscrow();
        this.cultureExpense = payment.getCultureExpense();
        this.secret = payment.getSecret();
        this.type = payment.getType();
        this.easyPay = payment.getEasyPay();
        this.taxFreeAmount = payment.getTaxFreeAmount();
        this.card = payment.getCard();
        this.requestApply = payment.getRequestApply();

        for (Cancel cancel : payment.getCancels()) {
            cancel.setPayment(this);
        }
    }

    private void verifyEqualsPaymentKeyAndOrderId(String paymentKey, String orderId) {
        if(!this.paymentKey.equals(paymentKey) || !this.orderId.equals(orderId)){
            throw new IllegalArgumentException("paymentKey 또는 orederId가 맞지 않습니다.");
        }
    }
}
