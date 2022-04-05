package com.onfree.core.entity.chatting;

import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@DiscriminatorValue(value = "information")
public class InformationChat extends Chatting{
    @Enumerated(EnumType.STRING)
    private InformationChatType type;

    @Builder
    public InformationChat(User sender, User receiver, RequestApply requestApply, InformationChatType type) {
        super(sender, receiver, requestApply);
        this.type = type;
    }

    public static InformationChat createInformationChat(RequestApply requestApply, InformationChatType type){
        return InformationChat.builder()
                .sender(null)
                .receiver(null)
                .requestApply(requestApply)
                .type(type)
                .build();
    }
}
