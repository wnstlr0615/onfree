package com.onfree.core.dto.external.toss.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onfree.core.entity.payment.TossPaymentStatus;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentDto {
    @JsonProperty(value = "mId")
    private String mId; //가맹점 ID
    private String version; // Payment 객체 응답 버전
    private String paymentKey; // 결제 건에 대한 고유 키값
    private String orderId; // 가맹점에서 발급한 고유 ID
    private String orderName; // 결제에 대한 주문명
    private String currency; //결제 할 때 사용한 통화 단위 원한인 KRW만 가능
    private String method; // 결제 수단
    private Long totalAmount; // 총 결제 금액
    private Long balanceAmount; // 취소할 수 있는 금액 (잔고)
    private Long suppliedAmount; //공급 가액
    private Long vat; // 부가세
    private TossPaymentStatus status; // 결제 처리 상태
    private Date requestedAt; // 결제 요청이 일어난 날짜와 시간 정보
    private Date approvedAt; // 결제 승일 시간
    private Boolean useEscrow; //에스크로 사용 여부
    private Boolean cultureExpense; // 문화비 지출 여부
    private String secret; // 가상계좌로 결제할 때 전달되는 입금 콜백을 검증하기 위한 값
    private String type; // 결제 타입 정보 (NORMAL, BILLING, CONNECTPAY) 중 하나
    private String easyPay; // 간편 결제 사용시 간편 결제 타입 정보
    private Long taxFreeAmount; //면세 금액

    //결제 관련 정보보
    private CardDto card; // 카드 결제시 카드 관련 정보

    @Override
    public String toString() {
        return "PaymentDto{" +
                "mId='" + mId + '\'' +
                ", version='" + version + '\'' +
                ", paymentKey='" + paymentKey + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderName='" + orderName + '\'' +
                ", currency='" + currency + '\'' +
                ", method='" + method + '\'' +
                ", totalAmount=" + totalAmount +
                ", balanceAmount=" + balanceAmount +
                ", suppliedAmount=" + suppliedAmount +
                ", vat=" + vat +
                ", status=" + status +
                ", requestedAt=" + requestedAt +
                ", approvedAt=" + approvedAt +
                ", useEscrow=" + useEscrow +
                ", cultureExpense=" + cultureExpense +
                ", secret='" + secret + '\'' +
                ", type='" + type + '\'' +
                ", easyPay='" + easyPay + '\'' +
                ", taxFreeAmount=" + taxFreeAmount +
                ", card=" + card +
                '}';
    }
}
