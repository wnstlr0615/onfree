package com.onfree.core.entity.portfolioroom;

import com.onfree.common.model.BaseEntity;
import com.onfree.core.entity.user.ArtistUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PortfolioRoom extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portFolioRoomId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "portfolioRoom")
    private ArtistUser artistUser;

    private String portfolioRoomURL;
    private String statusMessage;

    @Enumerated(EnumType.STRING)
    private PortfolioRoomStatus portfolioRoomStatus;
    
    //== 생성메서드 ==//
    public static PortfolioRoom createPortfolioRoom(ArtistUser artistUser, String portfolioRoomURL){
        return PortfolioRoom.builder()
                .artistUser(artistUser)
                .portfolioRoomURL(portfolioRoomURL)
                .statusMessage("")
                .portfolioRoomStatus(PortfolioRoomStatus.PUBLIC_PORTFOLIO_ROOM)
                .build();
    }

    public void updatePortfolioRoomUrl(String portfolioUrl) {
        this.portfolioRoomURL = portfolioUrl;
    }
}
