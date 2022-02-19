package com.onfree.core.entity.user;


import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
import com.onfree.core.entity.portfolioroom.PortfolioRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue(value = "A")
public class ArtistUser extends User{

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "portfolio_room_id ")
    @Setter
    private PortfolioRoom portfolioRoom;

    @Enumerated(EnumType.STRING)
    private StatusMark statusMark;

    @Builder
    public ArtistUser(Long userId, String name, String nickname, String email, String password, String newsAgency, String phoneNumber, BankInfo bankInfo, UserAgree userAgree, Boolean adultCertification, Gender gender, String profileImage, Boolean deleted, Role role, String portfolioUrl) {
        super(userId, name, nickname, email, password, newsAgency, phoneNumber, bankInfo, userAgree, adultCertification, gender, profileImage, deleted, role);

        this.statusMark = StatusMark.OPEN;
        this.portfolioRoom = PortfolioRoom.createPortfolioRoom(this, portfolioUrl);
    }

    @Override
    public void setDeleted() {
        super.setDeleted();
    }

    public void update(UpdateArtistUserDto.Request request) {
        final BankInfo bankInfo = BankInfo.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .build();
        super.update(bankInfo, request.getAdultCertification(), request.getNickname(), request.getNewsAgency(), request.getPhoneNumber(), request.getProfileImage());
        portfolioRoom.updatePortfolioRoomUrl(request.getPortfolioUrl());
    }

    public void updateStatusMark(String statusMark) {
        this.statusMark = StatusMark.valueOf(statusMark);
    }
}
