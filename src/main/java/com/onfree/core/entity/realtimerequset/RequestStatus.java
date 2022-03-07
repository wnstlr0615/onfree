package com.onfree.core.entity.realtimerequset;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestStatus {
    REQUEST_RECRUITING("모집 중인 상태", "모집중"),
    REQUEST_REQUESTING("의뢰 중인 상태", "의뢰중"),
    REQUEST_FINISH("의뢰 종료 상태", "마감"),
    REQUEST_DELETED("삭제된 상태", "삭제됨")
    ;
    private final String description;
    private final String displayStatus;
}
