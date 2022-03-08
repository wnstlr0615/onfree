package com.onfree.core.entity.portfolioroom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PortfolioRoomStatus {
    PUBLIC_PORTFOLIO_ROOM("공개 상태"),
    PRIVATE_PORTFOLIO_ROOM("비공개 상태"),
    CAN_NOT_USE_PORTFOLIO_ROOM("운영자로 인해 이용할 수 없는 상태"),
    ;
    private final String description;

    public boolean isPublic(){
        return this.equals(PUBLIC_PORTFOLIO_ROOM);
    }
}
