package com.onfree.core.service.chatting;

import com.onfree.common.error.code.ChattingErrorCode;
import com.onfree.common.error.code.RequestApplyErrorCode;
import com.onfree.common.error.exception.ChattingException;
import com.onfree.common.error.exception.RequestApplyException;
import com.onfree.core.dto.chatting.MessageChatDto;
import com.onfree.core.entity.chatting.MessageChat;
import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.UserRepository;
import com.onfree.core.repository.chatting.ChattingRepository;
import com.onfree.core.repository.chatting.EstimateSheetChatRepository;
import com.onfree.core.repository.payment.PaymentRepository;
import com.onfree.core.repository.requestappy.RequestApplyRepository;
import com.onfree.utils.TossComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChattingService {
    private final SimpMessagingTemplate messagingTemplate;

    private final TossComponent tossComponent;

    private final ChattingRepository chattingRepository;
    private final UserRepository userRepository;
    private final RequestApplyRepository requestApplyRepository;
    private final PaymentRepository paymentRepository;
    private final EstimateSheetChatRepository estimateSheetChatRepository;

    //private final String URL = "/topic/chatting/%d";
    private final String URL = "/topic/greetings/1";
    public void AddMessageChat(Long roomId, User user, MessageChatDto.Request messageChatDto) {
        messagingTemplate.convertAndSend(messageChatDto);
        log.info("roomId : {}, email : {}, message : {}", roomId, user.getEmail(), messageChatDto.getMessage());
    }

    public void test(Long roomId, MessageChatDto.Request messageChatDto) {
        messagingTemplate.convertAndSend(URL, messageChatDto);
        log.info("roomId : {},  message : {}", roomId,  messageChatDto.getMessage());
    }

    @Transactional
    public MessageChatDto.Response addMessage(Long applyId, User sender, MessageChatDto.Request messageChatDto) {
        // 의뢰 지원 매칭 찾기
        RequestApply requestApply = getRequestApply(applyId, sender);

        //받는 유저 찾기
        User receiver = getRecipient(requestApply, sender);

        //sender -> receiver  메시지 저장
        MessageChat messageChatEntity = sendMessage(sender, messageChatDto, requestApply, receiver);

        messagingTemplate.convertAndSend(messageChatDto);

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
        return saveMessageChat(
                createMessageChat(sender, receiver, requestApply, messageChatDto.getMessage())
        );
    }

    private MessageChatDto.Response getMessageChatDtoResponse(MessageChat messageChatEntity) {
        return MessageChatDto.Response.fromEntity(messageChatEntity);
    }
}
