package com.onfree.core.dto.chatting;

import com.onfree.core.entity.chatting.InformationChat;
import com.onfree.core.entity.chatting.InformationChatType;
import lombok.*;

public class InformationChatDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response{
        private String type;

        //== 생성 메서드 ==//
        public static Response createInformationChatDto(InformationChatType type){
            return Response.builder()
                    .type(type.name())
                    .build();

        }
        public static Response fromEntity(InformationChat informationChat){
            return Response.builder()
                    .type(informationChat.getType().name())
                    .build();
        }
    }
}
