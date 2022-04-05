package com.onfree.core.entity.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InformationChatType {
    DEPOSIT_DOWN_PAYMENT("계약금 입금완료 알림")
    ;
    private final String description;
}
