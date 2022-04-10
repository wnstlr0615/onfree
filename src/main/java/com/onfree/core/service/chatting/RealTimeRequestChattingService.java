package com.onfree.core.service.chatting;

import com.onfree.common.error.code.ChattingErrorCode;
import com.onfree.common.error.code.RequestApplyErrorCode;
import com.onfree.common.error.code.TossPaymentErrorCode;
import com.onfree.common.error.exception.ChattingException;
import com.onfree.common.error.exception.RequestApplyException;
import com.onfree.common.error.exception.TossPaymentException;
import com.onfree.core.dto.chatting.EstimateSheetChatDto;
import com.onfree.core.dto.chatting.MessageChatDto;
import com.onfree.core.dto.external.toss.payment.PaymentDto;
import com.onfree.core.dto.external.toss.refund.RefundRequestDto;
import com.onfree.core.entity.chatting.EstimateSheetChat;
import com.onfree.core.entity.chatting.MessageChat;
import com.onfree.core.entity.chatting.NotificationChat;
import com.onfree.core.entity.chatting.NotificationChatType;
import com.onfree.core.entity.payment.Payment;
import com.onfree.core.entity.payment.TossPaymentStatus;
import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.UserRepository;
import com.onfree.core.repository.chatting.ChattingRepository;
import com.onfree.core.repository.chatting.EstimateSheetChatRepository;
import com.onfree.core.repository.payment.PaymentRepository;
import com.onfree.core.repository.requestappy.RequestApplyRepository;
import com.onfree.utils.SimpMessageTemplateComponent;
import com.onfree.utils.TossComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeRequestChattingService {
    private final TossComponent tossComponent;
    private final SimpMessageTemplateComponent messageTemplateComponent;

    private final ChattingRepository chattingRepository;
    private final UserRepository userRepository;
    private final RequestApplyRepository requestApplyRepository;
    private final PaymentRepository paymentRepository;
    private final EstimateSheetChatRepository estimateSheetChatRepository;



    /** 메시지 전송하기 */
    @Transactional
    public MessageChatDto.Response addMessage(Long applyId, User sender, MessageChatDto.Request messageChatDto) {
        // 의뢰 지원 매칭 찾기
        RequestApply requestApply = getRequestApply(applyId, sender);

        //받는 유저 찾기
        User receiver = getRecipient(requestApply, sender);

        //sender -> receiver  메시지 전송
        MessageChat messageChatEntity = sendMessage(sender, messageChatDto, requestApply, receiver);

        //DTO Response로 변환
        return getMessageChatDtoResponse(messageChatEntity);
    }

    private RequestApply getRequestApply(Long applyId, User sender) {
        //TODO requestApply 상태에 따라 채팅 가능 여부 검증

        RequestApply requestApply = requestApplyRepository.findByRequestApplyId(applyId)
                .orElseThrow(() -> new RequestApplyException(RequestApplyErrorCode.NOT_FOUND_REQUEST_APPLY_ID));

        // 채팅 보내는 사용자가 해당 의뢰 지원에 포함되어 있는지 확인
        if(requestApply.notContainUser(sender)){
            throw new ChattingException(ChattingErrorCode.WRONG_REQUEST_APPLY_ID);
        }
        return requestApply;
    }

    private User getRecipient(RequestApply requestApply, User sender) {
        //TODO 유저 상태에 따라 메시지 전송 가능 여부 검증
        return requestApply.getReceiver(sender);
    }

    private MessageChat saveMessageChat(MessageChat messageChat) {
        return chattingRepository.save(messageChat);
    }

    private MessageChat createMessageChat(User sender, User receiver, RequestApply requestApply, String message) {
        return MessageChat.createMessageChat(sender, receiver, requestApply, message);
    }

    private MessageChat sendMessage(User sender, MessageChatDto.Request messageChatDto, RequestApply requestApply, User receiver) {
        //MessageChat Entity 저장
        MessageChat messageChat = createMessageChat(sender, receiver, requestApply, messageChatDto.getMessage());

        MessageChat saveMessageChat = saveMessageChat(messageChat);
        messageTemplateComponent.sendMessage(requestApply.getRequestApplyId(), saveMessageChat);
        return saveMessageChat;
    }

    private MessageChatDto.Response getMessageChatDtoResponse(MessageChat messageChatEntity) {
        return MessageChatDto.Response.fromEntity(messageChatEntity);
    }

    /** 견적서 전송*/
    @Transactional
    public EstimateSheetChatDto.Response addEstimateSheet(Long applyId, User sender, EstimateSheetChatDto.Request chatDto) {
        // 의뢰 지원 매칭 찾기
        RequestApply requestApply = getRequestApply(applyId, sender);

        //받는 유저 찾기
        User receiver = getRecipient(requestApply, sender);

        //의뢰 지원 상태 변경
        requestApply.changeStatusToReceivedStatement();

        //견적서 전송
        EstimateSheetChat estimateSheetChatEntity = sendEstimateSheetChat(sender, chatDto, requestApply, receiver);
        return getEstimateSheetChatDtoResponse(estimateSheetChatEntity);
    }

    private EstimateSheetChat sendEstimateSheetChat(User sender, EstimateSheetChatDto.Request chatDto, RequestApply requestApply, User receiver) {
        //EstimateSheetChat Entity 생성
        EstimateSheetChat estimateSheetChat = createEstimateSheetChat(requestApply, sender, receiver, chatDto);

        //EstimateSheetChat Entity 저장
        EstimateSheetChat saveEstimateSheetChat = saveEstimateSheetChat(estimateSheetChat);

        messageTemplateComponent.sendMessage(requestApply.getRequestApplyId(), saveEstimateSheetChat);
        return saveEstimateSheetChat;
    }

    private EstimateSheetChatDto.Response getEstimateSheetChatDtoResponse(EstimateSheetChat estimateSheetChat) {

        return EstimateSheetChatDto.Response.fromEntity(estimateSheetChat);
    }

    private EstimateSheetChat createEstimateSheetChat(RequestApply requestApply, User sender, User receiver, EstimateSheetChatDto.Request chatDto) {
        //토스 결제용  orderId 생성
        String orderId = UUID.randomUUID().toString();

        // EstimateSheetChat 생성
        return EstimateSheetChat.createEstimateSheetChat(
                requestApply, sender, receiver, chatDto.getTitle(), chatDto.getContent(), chatDto.getEstimatedAmount(),
                chatDto.getStartDate(), chatDto.getEndDate(), chatDto.getConditionNote(), chatDto.getOfferResult(), orderId
        );
    }

    private EstimateSheetChat saveEstimateSheetChat(EstimateSheetChat estimateSheetChat) {
        return chattingRepository.save(estimateSheetChat);
    }


    /** 지급 요청하기 */
    @Transactional
    public void requestPayment(Long applyId, User sender) {
        // 의뢰 지원 매칭 찾기
        RequestApply requestApply = getRequestApply(applyId, sender);

        //받는 유저 찾기
        User receiver = getRecipient(requestApply, sender);

        //의뢰 상태를 지급 요청 상태로 변경
        requestApply.changeStatusToPaymentRequest();

        //지급요청 알림 전송(의뢰자에게 알림 전송)
        sendNotification(requestApply, sender, receiver, NotificationChatType.PAYMENT_REQUEST_CLIENT);

        //지급요청 알림 전송(작가 유저에게 알림 전송)
        sendNotification(requestApply, sender, sender, NotificationChatType.PAYMENT_REQUEST_ARTIST);
    }

    private NotificationChat sendNotification(RequestApply requestApply, User sender, User receiver, NotificationChatType type) {
        //지급요청 알림 메시지 생성(의뢰자에게 보여지는 알림)
        NotificationChat notificationChat = createNotificationChat(requestApply, sender, receiver, type);

        NotificationChat saveNotification = chattingRepository.save(notificationChat);

        messageTemplateComponent.sendMessage(requestApply.getRequestApplyId(), saveNotification);
        return saveNotification;
    }

    private NotificationChat createNotificationChat(RequestApply requestApply, User sender, User receiver, NotificationChatType type) {
        return NotificationChat.createNotificationChat(requestApply, sender, receiver, type);
    }


    /** 결제 대금 지급 (거래 완료)*/
    @Transactional
    public void acceptPaymentRequest(Long applyId, User sender) {
        // 의뢰 지원 매칭 찾기
        RequestApply requestApply = getRequestApply(applyId, sender);

        //받는 유저 찾기
        User receiver = getRecipient(requestApply, sender);

        //결제 대금 지급 완료 상태로 변경 가능 여부 확인
        requestApply.changeStatusToPaymentCompleted();

        // 법인 계좌에서 작가 유저에게 돈 입금 기능(추가 구현 필요)
        sendMoneyFromCompanyToArtist(requestApply);

        //지급요청 완료 알림 전송(의뢰자에게 알림 전송)
        sendNotification(requestApply, sender, sender, NotificationChatType.PAYMENT_REQUEST_COMPLETED_CLIENT);

        //지급요청 완료 알림 전송(작가 유저에게 알림 전송)
        sendNotification(requestApply, sender, receiver, NotificationChatType.PAYMENT_REQUEST_COMPLETED_ARTIST);

    }

    private void sendMoneyFromCompanyToArtist(RequestApply requestApply) {
        EstimateSheetChat estimateSheetChat = getEstimateSheetChatByRequestApplyAndOrderedIsTrue(requestApply);
        Payment payment = getPaymentByOrderId(estimateSheetChat.getOrderId());
        //TODO 결제 대금 지급 입금 여부 확인 및 법인 계좌 -> 작가유저에게 전달(추가 구현 필요)
        log.info("법인 계좌에서 작가 계좌로 돈이 입급되었습니다.");
    }

    private Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RequestApplyException(RequestApplyErrorCode.NOT_FOUND_PAYMENT));
    }

    private EstimateSheetChat getEstimateSheetChatByRequestApplyAndOrderedIsTrue(RequestApply requestApply) {
        return estimateSheetChatRepository.findByRequestApplyAndOrderedIsTrue(requestApply)
                .orElseThrow(() -> new RequestApplyException(RequestApplyErrorCode.NOT_FOUND_ESTIMATE_SHEET));
    }

    /** 환불 요청하기 (결제 취소)*/
    @Transactional
    public void requestRefund(Long applyId, User sender) {
        // 의뢰 지원 매칭 찾기
        RequestApply requestApply = getRequestApply(applyId, sender);

        //받는 유저 찾기
        User receiver = getRecipient(requestApply, sender);

        //환불 요청 상태로 변경 가능 여부 확인
        requestApply.changeStatusToRefundRequest();

        //환불 요청 알림 전송(의뢰자에게 알림 전송)
        sendNotification(requestApply, sender, sender, NotificationChatType.REFUND_REQUEST_CLIENT);

        //환불 요청 알림 전송(작가 유저에게 알림 전송)
        sendNotification(requestApply, sender, receiver, NotificationChatType.REFUND_REQUEST_ARTIST);
    }



    /** 환불 요청 승인 하기*/
    @Transactional
    public void acceptRequestRefund(Long applyId, User sender, RefundRequestDto refundRequestDto) {
        // 의뢰 지원 매칭 찾기
        RequestApply requestApply = getRequestApply(applyId, sender);

        //받는 유저 찾기
        User receiver = getRecipient(requestApply, sender);

        //환불 요청 상태로 변경 가능 여부 확인
        requestApply.changeStatusToRefundCompleted();

        //TODO 지급 대행 API 사용 하여  결제를 취소해야하지만 지급 완료 후에는 환불 불가

        // 법인 계좌에서 작가 유저에게 돈 입금 기능(추가 구현 필요)
        sendRefundRequestToToss(requestApply, refundRequestDto);

        //환불 요청 완료 알림 전송(의뢰자에게 알림 전송)
        sendNotification(requestApply, sender, sender, NotificationChatType.REFUND_REQUEST_COMPLETE_CLIENT);

        //환불요청 완료 알림 전송(작가 유저에게 알림 전송)
        sendNotification(requestApply, sender, receiver, NotificationChatType.PAYMENT_REQUEST_COMPLETED_ARTIST);
    }

    private void sendRefundRequestToToss(RequestApply requestApply, RefundRequestDto refundRequestDto) {
        EstimateSheetChat estimateSheetChat = getEstimateSheetChatByRequestApplyAndOrderedIsTrue(requestApply);
        Payment payment = getPaymentByOrderId(estimateSheetChat.getOrderId());

        //결제 상태가 DONE인지 확인
        verifyPaymentStatusIsDone(payment);

        //향후 과세 면세 기능 추가 시 기존 코드 그대로 이용 (현재 )
        //환불 요청 dto 생성성
        RefundRequestDto requestDto = RefundRequestDto.createRefundRequestDto(refundRequestDto.getCancelReason());

        //토스 서버에 결제 취소 요청 보내기
        PaymentDto paymentDto = tossComponent.refundRequest(payment.getPaymentKey(), requestDto);

        //기존 Payment Update
        Payment updatePayment = paymentDto.toEntity();
        payment.updatePayment(updatePayment);

    }

    private void verifyPaymentStatusIsDone(Payment payment) {
        if(!payment.getStatus().equals(TossPaymentStatus.DONE)){
            throw new TossPaymentException(TossPaymentErrorCode.TOSS_PAYMENT_STATUS_IS_NOT_DONE);
        }
    }
}
