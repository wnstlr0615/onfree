package com.onfree.core.entity.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationChatType {
    PAYMENT_REQUEST_CLIENT("지급 요청(의뢰자에게 보여지는 알림)"),
    PAYMENT_REQUEST_ARTIST("지급 요청(작가에게 보여지는 알림)"),
    PAYMENT_REQUEST_COMPLETED_CLIENT("지급 요청 완료(의뢰자에게 보여지는 알림)"),
    PAYMENT_REQUEST_COMPLETED_ARTIST("지급 요청 완료(작가에게 보여지는 알림)"),
    REFUND_REQUEST_CLIENT("환불 요청(의뢰자에게 보여지는 알림)"),
    REFUND_REQUEST_ARTIST("환불 요청(작가에게 보여지는 알림)"),
    REFUND_REQUEST_ACCEPT_ARTIST("환분 요청 수락/취소(작가에게 보여지는 알림)"),
    REFUND_REQUEST_COMPLETE_CLIENT("환분 요청 완료(의뢰자에게 보여지는 알림)"),
    REFUND_REQUEST_COMPLETE_ARTIST("환분 요청 완료(작가에게 보여지는 알림)"),
    EXTRA_MONEY_REQUEST("추가금 요청")
    ;

    private final String description;

}
