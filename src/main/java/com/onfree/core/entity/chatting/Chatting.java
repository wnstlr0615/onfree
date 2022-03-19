package com.onfree.core.entity.chatting;

import com.onfree.common.model.BaseTimeEntity;
import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Chatting extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chattingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // 보내는 사람

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient; // 받는 사람


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_id")
    private RequestApply requestApply;

    public Chatting(User sender, User recipient, RequestApply requestApply) {
        this.sender = sender;
        this.recipient = recipient;
        this.requestApply = requestApply;
    }
}
