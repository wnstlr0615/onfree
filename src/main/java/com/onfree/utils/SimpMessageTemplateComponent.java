package com.onfree.utils;

import com.onfree.core.dto.chatting.ChatDto;
import com.onfree.core.entity.chatting.Chatting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpMessageTemplateComponent {
    private final String clientURLFormat = "/topic/chatting/%d";

    private final SimpMessagingTemplate simpMessagingTemplate;

    /** 메시지 보내기 */
    public void sendMessage(Long roomId, Chatting chatting){
        //목적지 설정
        String  destinationUrl = getDestinationUrl(roomId);
        // 전송 객체 설정
        ChatDto chatDto = ChatDto.fromEntity(chatting);

        //메시지 전송
        try {
            simpMessagingTemplate.convertAndSend(destinationUrl, chatDto);
        } catch (MessagingException e) {
            log.error("메시지 전송 에러 - roomId : {}, chattingId : {}", roomId, chatting.getChattingId());
            log.error(e.getMessage());
        }
    }

    private String getDestinationUrl(Long roomId) {
        return String.format(clientURLFormat, roomId);
    }
}
