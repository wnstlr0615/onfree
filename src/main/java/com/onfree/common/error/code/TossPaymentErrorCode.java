package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TossPaymentErrorCode implements ErrorCode{
    NOT_FOUND_ORDER_ID("해당 orderId를 가진 견적서가 없습니다.", 400),
    BAD_REQUEST_NOT_EQUALS_AMOUNT("견적서와 금액이 맞지 않는 잘못된 요청입니다.", 400),
    REQUEST_APPLY_STATUS_UNABLE_TO_PAY("해당 의뢰 상태는 현재 결제가 불가능한 상태입니다.", 400),
    TOSS_PAYMENT_REQUEST_IS_BAD_REQUEST("잘못된 요청에 따라 결제를 처리할 수 없습니다.", 400),
    TOSS_PAYMENT_REQUEST_IS_FORBIDDEN("결제 시크릿 키가 없거나 잘못된 키입니다.", 500),
    TOSS_PAYMENT_REQUEST_IS_NOT_FOUND("요청한 리소스가 존재 하지 않습니다.", 400),
    TOSS_SERVER_ERROR("결제 서버에 에러가 발생하였습니다.", 500),

    TOSS_PAYMENT_REQUEST_IS_FAIL("알 수 없는 오류로 인해 결제가 실패하였습니다.", 500),

    TOSS_PAYMENT_STATUS_IS_NOT_DONE("결제 상태가 환불 불가 상태입니다.", 400),

    //== 결제 승인 관련 에러 코드 ==//
    EXCEED_MAX_CARD_INSTALLMENT_PLAN("설정 가능한 최대 할부 개월 수를 초과했습니다.",400),
    EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT("1회 출금 한도를 초과했습니다.",400),
    EXCEED_MAX_PAYMENT_AMOUNT("하루 결제 가능 금액을 초과했습니다.",400),
    INVALID_ACCOUNT_NUMBER_OR_UNAVAILABLE("잘못된 계좌번호이거나 서비스 불가한 계좌입니다.",400),
    INVALID_AUTHORIZE_AUTH("잘못된 인증 방식입니다.",400),
    INVALID_CARD_IDENTITY("입력하신 주민번호/사업자번호가 카드 소유주 정보와 일치하지 않습니다.",400),
    INVALID_OTP("OTP 번호가 잘못되었거나 타기관 응답 오류 입니다",400),
    NOT_FOUND_TERMINAL_ID("단말기번호(Terminal Id)가 없습니다. 토스페이먼츠로 문의 바랍니다.",400),
    NOT_REGISTERED_BUSINESS("미등록된 사업자 입니다.",400),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN("할부 또는 무이자 할부가 지원되지 않는 카드입니다.",400),

    //== 결제 취소 관련 에러 코드 ==//
    ALREADY_CANCELED_PAYMENT("이미 취소된 결제 입니다.", 400),
    INVALID_BANK("유효하지 않은 은행입니다.", 400),
    INVALID_REFUND_ACCOUNT_INFO("환불 계좌번호와 예금주명이 일치하지 않습니다.", 400),
    INVALID_REFUND_ACCOUNT_NUMBER("잘못된 환불 계좌번호입니다.", 400),
    NOT_MATCHES_REFUNDABLE_AMOUNT("잔액 결과가 일치하지 않습니다.", 400),
    EXCEED_MAX_REFUND_DUE("환불 가능한 기간이 초과했습니다.", 403),
    FORBIDDEN_REQUEST("허용되지 않은 요청입니다.", 400),
    NOT_ALLOWED_PARTIAL_REFUND("에스크로 주문, 현금 카드 결제 등의 사유로 부분 환불이 불가합니다.", 403),
    NOT_ALLOWED_PARTIAL_REFUND_WAITING_DEPOSIT("입금 대기중인 결제는 부분 환불이 불가합니다.", 403),
    NOT_CANCELABLE_AMOUNT("취소 할 수 없는 금액 입니다.", 403),
    NOT_CANCELABLE_PAYMENT("취소 할 수 없는 결제 입니다.", 403),
    INVALID_REFUND_AMOUNT("잘못된 환불 금액입니다.", 403),
    NOT_FOUND_METHOD("존재하지 않는 결제 수단 입니다.", 404),
    NOT_FOUND_PAYMENT("존재하지 않는 결제 정보 입니다.", 404),
    FAILED_METHOD_HANDLING_CANCEL("취소 중 결제 시 사용한 결제 수단 처리과정에서 일시적인 오류가 발생했습니다", 500),
    FAILED_REFUND_PROCESS("은행 응답시간 지연이나 일시적인 오류로 환불요청에 실패했습니다.", 500),
    ;

    private final String description;
    private final int status;
}
