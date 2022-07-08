package com.onfree.core.dto.chatting;

import com.onfree.core.entity.chatting.NotificationChat;
import lombok.*;

public class NotificationChatDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        private String type;
        public static Response fromEntity(NotificationChat notificationChat) {
            return Response.builder()
                    .type(notificationChat.getType().name())
                    .build();
        }
    }
}
