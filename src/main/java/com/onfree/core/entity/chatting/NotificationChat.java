package com.onfree.core.entity.chatting;

import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue(value = "notification")
public class NotificationChat extends Chatting{
    private String message;

    @Builder
    public NotificationChat(User sender, User recipient, RequestApply requestApply, String message) {
        super(sender, recipient, requestApply);
        this.message = message;
    }

    public static NotificationChat createNotificationChat(RequestApply requestApply, User sender, User recipient, String message) {
        return NotificationChat.builder()
                .requestApply(requestApply)
                .sender(sender)
                .recipient(recipient)
                .message(message)
                .build();
    }
}
