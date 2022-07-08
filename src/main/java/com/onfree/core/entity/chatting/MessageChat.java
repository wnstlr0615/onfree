package com.onfree.core.entity.chatting;

import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@DiscriminatorValue(value = "message")
public class MessageChat extends Chatting{
    private String message;

    @Builder
    public MessageChat(User sender, User receiver, RequestApply requestApply, String message) {
        super(sender, receiver, requestApply);
        this.message = message;
    }

    public static MessageChat createMessageChat(User sender, User receiver, RequestApply requestApply, String message) {
        return MessageChat.builder()
                .sender(sender)
                .receiver(receiver)
                .requestApply(requestApply)
                .message(message)
                .build();
    }
}
