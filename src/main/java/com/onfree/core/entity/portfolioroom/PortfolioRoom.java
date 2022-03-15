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

    @Column(nullable = false, unique = true)
    private String portfolioRoomURL;

    private String statusMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortfolioRoomStatus portfolioRoomStatus;
    
    //== 생성 메서드 ==//
    public static PortfolioRoom createPortfolioRoom(ArtistUser artistUser, String portfolioRoomURL){
        return createPortfolioRoom(artistUser, portfolioRoomURL, "", PortfolioRoomStatus.PUBLIC_PORTFOLIO_ROOM);
    }

    public static PortfolioRoom createPortfolioRoom(ArtistUser artistUser, String portfolioRoomURL, String statusMessage, PortfolioRoomStatus publicPortfolioRoom){
        return PortfolioRoom.builder()
                .artistUser(artistUser)
                .portfolioRoomURL(portfolioRoomURL)
                .statusMessage(statusMessage)
                .portfolioRoomStatus(publicPortfolioRoom)
                .build();
    }

    //== 비즈니스 로직 ==//
    public void updatePortfolioRoomUrl(String portfolioUrl) {
        this.portfolioRoomURL = portfolioUrl;
    }

    public void updatePortfolioRoomStatus(PortfolioRoomStatus status) {
        this.portfolioRoomStatus = status;
    }

    public void updateStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
