package com.onfree.core.dto.external.toss.payment.approval;

import io.swagger.annotations.ApiModel;
import lombok.*;

/** 결제 승인 DTO */

public class PaymentApprovalReqDto {
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Getter
    public static class Request{
       private String orderId;
       private Long amount;

       public static Request createPaymentApprovalDto(String orderId, Long amount) {
           return Request.builder()
                   .orderId(orderId)
                   .amount(amount)
                   .build();
       }
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Getter
    public static class Response{
        private String orderId;
        private Long amount;
        //TODO 결제 후 응답 데이터 추가
        public static Response createPaymentApprovalDto(String orderId, Long amount) {
            return Response.builder()
                    .orderId(orderId)
                    .amount(amount)
                    .build();
        }
    }
}
