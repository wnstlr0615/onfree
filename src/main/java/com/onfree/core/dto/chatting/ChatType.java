package com.onfree.core.dto.chatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatType {
    MESSAGE("메시지"),
    ESTIMATE_SHEET("견적서"),
    PROPOSAL_SHEET("제안서"),
    NOTIFICATION("알림창"),
    INFORMATION("공지창")
    ;
    private final String description;
}
