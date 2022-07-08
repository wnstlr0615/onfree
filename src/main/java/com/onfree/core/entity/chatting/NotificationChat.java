package com.onfree.core.entity.chatting;

import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "notification")
public class NotificationChat extends Chatting{

    @Enumerated(EnumType.STRING)
    private NotificationChatType type;

    @Builder
    public NotificationChat(User sender, User receiver, RequestApply requestApply, String message, NotificationChatType type) {
        super(sender, receiver, requestApply);
        this.type = type;
    }

    //== 생성 메서드 ==//
    public static NotificationChat createNotificationChat(RequestApply requestApply, User sender, User receiver, NotificationChatType type) {
        return NotificationChat.builder()
                .requestApply(requestApply)
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .build();
    }

}
