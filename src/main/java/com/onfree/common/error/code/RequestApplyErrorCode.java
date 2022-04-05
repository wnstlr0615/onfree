package com.onfree.common.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestApplyErrorCode implements ErrorCode{
    NOT_FOUND_REQUEST_APPLY_ID("해당 의뢰 요청 아이디를 찾을 수 없습니다.", 400),
    REQUEST_APPLY_DOES_NOT_CONTAIN_SENDER("해당 의뢰에 송신자가 없습니다.", 400),
    CAN_NOT_APPLY_REAL_TIME_REQUEST_DELETED("해당 의뢰는 삭제되어 지원이 불가합니다.", 400),
    CAN_NOT_APPLY_REAL_TIME_REQUEST_FINISHED("해당 의뢰는 마감되 어 지원이 불가합니다.", 400),
    CAN_NOT_APPLY_REAL_TIME_REQUEST_REQUESTING("현재 의뢰 중이므로 지원이 불가합니다.", 400),



    ALREADY_ACCEPT("이미 견적서(제안서)를 수락 하였습니다.", 400),
    DO_NOT_HAVE_RECEIVED_ESTIMATE("아직 견적서(제안서)를 받지 못했습니다.", 400),
    CAN_NOT_APPLY_MY_REAL_TIME_REQUEST("자신이 작성한 실시간 의뢰에 본인은 지원 할 수 없습니다.", 400),
    ALREADY_REQUEST_APPLY("이미 지원 하였습니다.", 400),


    //== 의뢰 상태에 따른 결제 관련 에러 ==//
    CAN_NOT_RECEIVED_STATEMENT("견적서 또는 제안서를 받을 수 없는 상태입니다.", 400),
    DO_NOT_GET_STATEMENT("아직 견적서(제안서)를 받지 못한 상태입니다.", 400),
    ALREADY_DEPOSIT_PAYMENT("이미 계약금을 입금하였습니다.", 400),
    CAN_NOT_DEPOSIT_CANCELED_TRANSACTION("거래가 취소되어 결제가 불가합니다.", 400),

    //== 지급요청 관련 에러 ==//
    NO_DOWN_PAYMENT_HAS_BEEN_MADE("계약금이 입급되지 않은 상태입니다.", 400),
    REQUEST_APPLY_IS_CANCEL("해당 의뢰는 취소 되었습니다.", 400),
    ALREADY_REQUEST_PAYMENT("이미 지급요청을 완료하였습니다.", 400),
    DO_NOT_PAYMENT_REQUEST("작가유저가 결제 지급요청을 아직 하지 않았습니다.",400),
    ALREADY_PAYMENT_REQUEST_COMPLETED("이미 결제 대금 지급이 완료되었습니다. ", 400),

    //== 견적서 찾기 에러 ==//
    NOT_FOUND_ESTIMATE_SHEET("결제를 한 견적서를 찾지 못했습니다.", 400),

    //== PAYMENT 결제 관련 에러 ==//
    NOT_FOUND_PAYMENT("해당 orderId 에대한 payment 를 찾을 수 없습니다.", 400),

    //== 환불 관련 에러 ==//
    CAN_NOT_REFUND_REQUEST("환불 요청 할 수 없는 상태입니다.", 400),
    NOT_GET_REFUND_REQUEST("환불 요청 상태가 오지 않았습니다..", 400),
    ALREADY_CANCELED_PAYMENT("이미 환불도니 결제 입니다.", 400),
;
    private final String description;
    private final int status;
}
