package com.onfree.core.dto.chatting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onfree.core.entity.chatting.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatDto {
    private String sender; // 보내는 사람
    private String receiver; // 받는 사람
    private ChatType chatType; // 채팅 타입
    private Object content; // 내용
    private LocalDateTime createDate;

    //== 생성 메서드 ==//
    private static ChatDto createChatDto(String sender, String receiver, ChatType chatType, Object content, LocalDateTime createDate){
        return ChatDto.builder()
                .sender(sender)
                .receiver(receiver)
                .chatType(chatType)
                .content(content)
                .createDate(createDate)
                .build();
    }

    public static ChatDto fromEntity(Chatting chatting){
        if(chatting instanceof MessageChat){
            MessageChat messageChat = (MessageChat) chatting;
            MessageChatDto.Response content = MessageChatDto.Response.fromEntity(messageChat);
            return ChatDto.createMessageChatDto(messageChat.getSender().getNickname(), messageChat.getReceiver().getNickname(), content, messageChat.getCreatedDate());

        }else if(chatting instanceof EstimateSheetChat){
            EstimateSheetChat estimateSheetChat = (EstimateSheetChat) chatting;
            EstimateSheetChatDto.Response content = EstimateSheetChatDto.Response.fromEntity(estimateSheetChat);
            return ChatDto.createEstimateSheetChatDto(estimateSheetChat.getSender().getNickname(), estimateSheetChat.getReceiver().getNickname(), content, estimateSheetChat.getCreatedDate());

        }else if(chatting instanceof InformationChat){
            InformationChat informationChat = (InformationChat) chatting;
            InformationChatDto.Response content = InformationChatDto.Response.fromEntity(informationChat);
            return ChatDto.createInformationChatDto(content, informationChat.getCreatedDate());

        }else if(chatting instanceof NotificationChat ){
            NotificationChat notificationChat = (NotificationChat) chatting;
            NotificationChatDto.Response content = NotificationChatDto.Response.fromEntity(notificationChat);
            return ChatDto.createNotificationChatDto(notificationChat.getSender().getNickname(), notificationChat.getReceiver().getNickname(), content, notificationChat.getCreatedDate());

        } else{
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }
    public static ChatDto createMessageChatDto(String sender, String receiver, MessageChatDto.Response content, LocalDateTime createDate){
        return ChatDto.createChatDto(sender,receiver, ChatType.MESSAGE, content, createDate);
    }

    public static ChatDto createEstimateSheetChatDto(String sender, String receiver, EstimateSheetChatDto.Response content, LocalDateTime createDate){
        return ChatDto.createChatDto(sender,receiver, ChatType.ESTIMATE_SHEET, content, createDate);
    }

    public static ChatDto createNotificationChatDto(String sender, String receiver, NotificationChatDto.Response content, LocalDateTime createDate){
        return ChatDto.createChatDto(sender, receiver, ChatType.NOTIFICATION, content, createDate);
    }

    public static ChatDto createInformationChatDto(InformationChatDto.Response content, LocalDateTime createDate){
        return ChatDto.createChatDto(null, null, ChatType.INFORMATION, content, createDate);
    }


}
