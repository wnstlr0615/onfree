package com.onfree.core.service.external.toss;

import com.onfree.common.error.code.TossPaymentErrorCode;
import com.onfree.common.error.exception.RequestApplyException;
import com.onfree.common.error.exception.TossPaymentException;
import com.onfree.core.dto.external.toss.payment.PaymentDto;
import com.onfree.core.dto.external.toss.payment.approval.PaymentApprovalReqDto;
import com.onfree.core.entity.chatting.*;
import com.onfree.core.entity.payment.Payment;
import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.chatting.ChattingRepository;
import com.onfree.core.repository.chatting.EstimateSheetChatRepository;
import com.onfree.core.repository.payment.PaymentRepository;
import com.onfree.utils.TossComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossPaymentService {
    private final TossComponent tossComponent;
    private final EstimateSheetChatRepository estimateSheetChatRepository;
    private final PaymentRepository paymentRepository;
    private final ChattingRepository chattingRepository;


    /** 결제 승인 요청 */
    @Transactional
    public void requestApproval(String paymentKey, String orderId, Long amount) {
        log.info("request approval start");
        log.debug("paymentKey : {}, orderId : {}, amount : {}", paymentKey, orderId, amount);
        //견적서 Entity 조회
        EstimateSheetChat estimateSheetChatEntity = getEstimateSheetChatEntity(orderId, amount);
        RequestApply requestApply = estimateSheetChatEntity.getRequestApply();

        // 해당 지원상태가 결제 가능한 상태인지 확인
        verifyRequestApplyStatusIsAcceptableForPayment(requestApply);

        //결제 승인 요청 DTO 생성
        PaymentApprovalReqDto.Request body = createPaymentApprovalRequestDto(orderId, amount);

        //결제 승인 요청
        PaymentDto paymentDto = tossComponent.paymentRequestApproval(paymentKey, body);

        //계약금 입금 상태로 변경
        requestApply.changeStatusToDepositDownPayment();

        //견적서 상태 ordered 를 true 로 수정
        estimateSheetChatEntity.ordered();

        //결제 관련 Entity 생성
        Payment payment = createPaymentFromDto(paymentDto, requestApply);

        //Payment Entity 저장
        savePayment(payment);

        //계약금 지불 완료 공지 Chat 저장
        saveInformationChatOfDepositDownPayment(requestApply);

        log.info("request approval end");
    }

    private void saveInformationChatOfDepositDownPayment(RequestApply requestApply) {
        //계약금 지불 완료 알림 저장
        InformationChat informationChat = InformationChat.createInformationChat(requestApply, InformationChatType.DEPOSIT_DOWN_PAYMENT);
        chattingRepository.save(informationChat);
    }

    private Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    private Payment createPaymentFromDto(PaymentDto paymentDto, RequestApply requestApply) {
        Payment payment = paymentDto.toEntity();
        payment.setRequestApply(requestApply);
        return payment;
    }

    private void verifyRequestApplyStatusIsAcceptableForPayment(RequestApply requestApply) {
        try {
            requestApply.verifyStatusIsAcceptableForPayment();
        } catch (RequestApplyException e) {
            log.error("validateRequestApplyPayable is fail - errorCode : {} , errorMessage : {} ", e.getErrorCode(), e.getErrorMessage());
            throw new TossPaymentException(TossPaymentErrorCode.NOT_FOUND_ORDER_ID);
        }
    }

    private EstimateSheetChat getEstimateSheetChatEntity(String orderId, Long amount) {
        EstimateSheetChat estimateSheetChat = estimateSheetChatRepository.findByOrderId(orderId)
                .orElseThrow(() -> new TossPaymentException(TossPaymentErrorCode.NOT_FOUND_ORDER_ID));

        //견적서 금액과 요청 금액이 맞지 않는 경우
        if(!estimateSheetChat.getPaymentAmount().equals(amount)){
            throw new TossPaymentException(TossPaymentErrorCode.BAD_REQUEST_NOT_EQUALS_AMOUNT);
        }
        return estimateSheetChat;
    }

    private PaymentApprovalReqDto.Request createPaymentApprovalRequestDto(String orderId, Long amount) {
        return PaymentApprovalReqDto.Request.createPaymentApprovalDto(orderId, amount);
    }


}
