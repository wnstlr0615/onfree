package com.onfree.core.entity.chatting;

import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@DiscriminatorValue(value = "proposal")
@AllArgsConstructor
@Entity
public class ProposalSheetChat extends Chatting{
    @Builder
    public ProposalSheetChat(User sender, User receiver, RequestApply requestApply) {
        super(sender, receiver, requestApply);
    }

    public static ProposalSheetChat createProposalSheetChat(User sender, User receiver, RequestApply requestApply){
        return ProposalSheetChat.builder()
                .sender(sender)
                .receiver(receiver)
                .requestApply(requestApply)
                .build();

    }
}
