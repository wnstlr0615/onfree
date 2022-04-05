package com.onfree.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.code.TossPaymentErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.common.error.exception.TossPaymentException;
import com.onfree.core.dto.external.toss.TossErrorRes;
import com.onfree.core.dto.external.toss.payment.PaymentDto;
import com.onfree.core.dto.external.toss.payment.approval.PaymentApprovalReqDto;
import com.onfree.core.dto.external.toss.refund.RefundRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossComponent {
    private final RestTemplateUtils restTemplateUtils;
    private final JsonUtils jsonUtils;

    @Value(value = "${external.toss.secret-key}")
    private String secretKey;

    /** 결제 승인 요청 하기 */
    public PaymentDto paymentRequestApproval(String paymentKey, PaymentApprovalReqDto.Request refundRequestDto){
        String PAYMENT_REQUEST_APPROVAL_URI = "https://api.tosspayments.com/v1/payments/{paymentKey}".replace("{paymentKey}", paymentKey);

        //RequestEntity 생성
        RequestEntity<String> requestEntity = getPostRequestEntity(PAYMENT_REQUEST_APPROVAL_URI, refundRequestDto);

        //결제 승인 요청 후 응답 받기
        ResponseEntity<String> responseEntity = restTemplateUtils.sendRequest(requestEntity);

        // 결제 승인 요청이 정상적인지 검증
        validateResponseStatusCodeIsSuccess(responseEntity, "토스 결제 승인 요청");

        return getPaymentDto(responseEntity);
    }

    private TossPaymentErrorCode getTossPaymentErrorCode(TossErrorRes tossErrorRes) {
        try {
            return TossPaymentErrorCode.valueOf(tossErrorRes.getCode());
        } catch (IllegalArgumentException e) {
            return TossPaymentErrorCode.TOSS_PAYMENT_REQUEST_IS_BAD_REQUEST;
        }
    }

    private TossErrorRes getTossErrorRes(ResponseEntity<String> responseEntity) {
        try {
            return jsonUtils.fromJson(responseEntity.getBody(), TossErrorRes.class);
        } catch (JsonProcessingException e) {
            log.error("토스 결제 승인 요청 실패 - TossErrorRes.class 로 JsonParsing 실패 ");
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private PaymentDto getPaymentDto(ResponseEntity<String> responseEntity) {
        try {
            return jsonUtils.fromJson(responseEntity.getBody(), PaymentDto.class);
        } catch (JsonProcessingException e) {
            log.error("토스 결제 승인 요청 성공 - PaymentDto.class 변환 중 JsonProcessingException 발생");
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /** 결제 취소 */
    public PaymentDto refundRequest(String paymentKey, RefundRequestDto refundRequestDto){
        String REFUND_REQUEST_URI = "https://api.tosspayments.com/v1/payments/{paymentKey}/cancel".replace("{paymentKey}", paymentKey);

        RequestEntity<String> requestEntity = getPostRequestEntity(REFUND_REQUEST_URI, refundRequestDto);


        //토스 서버에 결제 취소 요청
        ResponseEntity<String> responseEntity = restTemplateUtils.sendRequest(requestEntity);

        // 환불 요청이 정상적인지 검증
        validateResponseStatusCodeIsSuccess(responseEntity, "토스 환불 요청");

        //응답 PaymentDto 반환
        return getPaymentDto(responseEntity);
    }

    private void validateResponseStatusCodeIsSuccess(ResponseEntity<String> responseEntity, String methodName) {
        HttpStatus statusCode = responseEntity.getStatusCode();
        if(statusCode.is2xxSuccessful()){
            return;
        }
        //요청 실패 시 ErrorCode 로 응답 받기
        TossErrorRes tossErrorRes = getTossErrorRes(responseEntity);

        log.error("{} 실패 - statusCode :  {} ",methodName, statusCode.name());
        log.error("code : {}, message : {} ",tossErrorRes.getCode(), tossErrorRes.getMessage() );

        //error code 로 변환
        TossPaymentErrorCode tossPaymentErrorCode = getTossPaymentErrorCode(tossErrorRes);

        switch (statusCode){
            case BAD_REQUEST:
                throw new TossPaymentException(tossPaymentErrorCode);
            case FORBIDDEN:
                throw new TossPaymentException(TossPaymentErrorCode.TOSS_PAYMENT_REQUEST_IS_FORBIDDEN);
            case NOT_FOUND:
                throw new TossPaymentException(TossPaymentErrorCode.TOSS_PAYMENT_REQUEST_IS_NOT_FOUND);
            case INTERNAL_SERVER_ERROR:
                throw new TossPaymentException(TossPaymentErrorCode.TOSS_SERVER_ERROR);
            default:
                throw new TossPaymentException(TossPaymentErrorCode.TOSS_PAYMENT_REQUEST_IS_FAIL);
        }
    }

    private RequestEntity<String> getPostRequestEntity(String requestURI, Object body) {
        try {
            return RequestEntity.post(requestURI)
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes()))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(
                           jsonUtils.toJson(body)
                    );
        } catch (JsonProcessingException e) {
            log.error("TossComponent Error");
            log.error("JsonProcessingException ", e);
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }





}
